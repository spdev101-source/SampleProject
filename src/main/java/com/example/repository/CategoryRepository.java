package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Long> {
boolean existsByCategoryname(String categoryname);
boolean existsByCategorynameAndIdNot(String name,Long id);
/*
 Stock Report 
 
From Date:    [01-Aug-2026] 
To Date:      [31-Aug-2026] 
Product:      [All] 
Warehouse:    [Pune] 
 
                 Generate 
--------------------------------------------------------- 
Product | Warehouse | Opening | IN | OUT | Closing 
--------------------------------------------------------- 
Laptop  | Pune      | 100     | 50 | 30  | 120 
Monitor | Pune      | 80      | 20 | 15  | 85 
--------------------------------------------------------- 
 
package com.example.dto.response;

public class StockSummaryRowDTO {

	private String productName;
	private String warehouseName;
	private Integer opening;
	private Integer in;
	private Integer out;
	private Integer closing;

	public StockSummaryRowDTO(String productName, String warehouseName, Integer opening, Integer in, Integer out) {
		this.productName = productName;
		this.warehouseName = warehouseName;
		this.opening = opening;
		this.in = in;
		this.out = out;
		this.closing = opening + in - out;
	}

	public String getProductName() { return productName; }
	public String getWarehouseName() { return warehouseName; }
	public Integer getOpening() { return opening; }
	public Integer getIn() { return in; }
	public Integer getOut() { return out; }
	public Integer getClosing() { return closing; }
}
// OpeningStockRepository
@Query("SELECT SUM(o.quantity) FROM OpeningStock o " +
       "WHERE o.product.productId = :productId AND o.warehouse.warehouseId = :warehouseId " +
       "AND o.openingDate < :fromDate")
Long getOpeningSum(Long productId, Long warehouseId, LocalDate fromDate);

@Query("SELECT DISTINCT o.product.productId FROM OpeningStock o WHERE o.warehouse.warehouseId = :warehouseId")
List<Long> findProductIdsByWarehouse(Long warehouseId);
// PurchaseItemRepository
@Query("SELECT SUM(i.quantity) FROM PurchaseItem i " +
       "WHERE i.product.productId = :productId AND i.purchase.warehouse.warehouseId = :warehouseId " +
       "AND i.purchase.purchaseDate BETWEEN :fromDate AND :toDate")
Long getPurchaseSum(Long productId, Long warehouseId, LocalDate fromDate, LocalDate toDate);
// DCItemRepository
@Query("SELECT SUM(i.quantity) FROM DCItem i " +
       "WHERE i.product.productId = :productId AND i.dc.warehouse.warehouseId = :warehouseId " +
       "AND i.dc.dcDate BETWEEN :fromDate AND :toDate")
Long getDispatchSum(Long productId, Long warehouseId, LocalDate fromDate, LocalDate toDate);
// SalesReturnItemRepository
@Query("SELECT SUM(i.quantity) FROM SalesReturnItem i " +
       "WHERE i.product.productId = :productId AND i.salesReturn.warehouse.warehouseId = :warehouseId " +
       "AND i.salesReturn.returnDate BETWEEN :fromDate AND :toDate")
Long getReturnSum(Long productId, Long warehouseId, LocalDate fromDate, LocalDate toDate);

@Override
public List<StockSummaryRowDTO> getStockReport(Long warehouseId, Long productId, LocalDate fromDate, LocalDate toDate) {

    if (fromDate == null || toDate == null) {
        throw new RuntimeException("fromDate and toDate are required");
    }
    if (fromDate.isAfter(toDate)) {
        throw new RuntimeException("fromDate cannot be after toDate");
    }

    Warehouse warehouse = warehouseRepository.findById(warehouseId)
            .orElseThrow(() -> new RuntimeException("Warehouse not found"));

    List<Long> productIds;
    if (productId != null) {
        productIds = List.of(productId);
    } else {
        productIds = openingStockRepository.findProductIdsByWarehouse(warehouseId);
    }

    List<StockSummaryRowDTO> report = new ArrayList<>();

    for (Long pid : productIds) {

        Product product = productRepository.findById(pid)
                .orElseThrow(() -> new RuntimeException("Product not found: " + pid));

        Long openingResult = openingStockRepository.getOpeningSum(pid, warehouseId, fromDate);
        int opening;
        if (openingResult == null) {
            opening = 0;
        } else {
            opening = openingResult.intValue();
        }

        Long purchaseResult = purchaseItemRepository.getPurchaseSum(pid, warehouseId, fromDate, toDate);
        int purchaseIn;
        if (purchaseResult == null) {
            purchaseIn = 0;
        } else {
            purchaseIn = purchaseResult.intValue();
        }

        Long returnResult = salesReturnItemRepository.getReturnSum(pid, warehouseId, fromDate, toDate);
        int returnIn;
        if (returnResult == null) {
            returnIn = 0;
        } else {
            returnIn = returnResult.intValue();
        }

        Long dispatchResult = dcItemRepository.getDispatchSum(pid, warehouseId, fromDate, toDate);
        int dispatchOut;
        if (dispatchResult == null) {
            dispatchOut = 0;
        } else {
            dispatchOut = dispatchResult.intValue();
        }

        int in = purchaseIn + returnIn;
        int out = dispatchOut;

        report.add(new StockSummaryRowDTO(product.getProductName(), warehouse.getWarehouseName(), opening, in, out));
    }

    return report;
}
List<StockSummaryRowDTO> getStockReport(Long warehouseId, Long productId, LocalDate fromDate, LocalDate toDate);
@GetMapping("/stock-report")
public List<StockSummaryRowDTO> getStockReport(
        @RequestParam Long warehouseId,
        @RequestParam(required = false) Long productId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
    return openingStockService.getStockReport(warehouseId, productId, fromDate, toDate);
}
GET /api/opening-stocks/stock-report?warehouseId=1&fromDate=2026-08-01&toDate=2026-08-31
[
  { "productName": "Laptop", "warehouseName": "Pune", "opening": 100, "in": 50, "out": 30, "closing": 120 },
  { "productName": "Monitor", "warehouseName": "Pune", "opening": 80, "in": 20, "out": 15, "closing": 85 }
]
CREATE OR REPLACE FUNCTION get_stock_report(
    p_warehouse_id BIGINT,
    p_product_id BIGINT,
    p_from_date DATE,
    p_to_date DATE
)
RETURNS TABLE (
    product_name TEXT,
    warehouse_name TEXT,
    opening INTEGER,
    in_qty INTEGER,
    out_qty INTEGER
)
AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.product_name,
        w.warehouse_name,
        COALESCE((
            SELECT SUM(os.quantity) FROM opening_stocks os
            WHERE os.product_id = p.product_id
            AND os.warehouse_id = p_warehouse_id
            AND os.opening_date < p_from_date
        ), 0)::INTEGER AS opening,
        COALESCE((
            SELECT SUM(pi.quantity) FROM purchase_items pi
            JOIN purchases pu ON pi.purchase_id = pu.id
            WHERE pi.product_id = p.product_id
            AND pu.warehouse_id = p_warehouse_id
            AND pu.purchase_date BETWEEN p_from_date AND p_to_date
        ), 0)::INTEGER
        +
        COALESCE((
            SELECT SUM(sri.quantity) FROM sales_return_items sri
            JOIN sales_returns sr ON sri.return_id = sr.return_id
            WHERE sri.product_id = p.product_id
            AND sr.warehouse_id = p_warehouse_id
            AND sr.return_date BETWEEN p_from_date AND p_to_date
        ), 0)::INTEGER AS in_qty,
        COALESCE((
            SELECT SUM(dci.quantity) FROM dc_items dci
            JOIN dcs dc ON dci.dc_id = dc.dc_id
            WHERE dci.product_id = p.product_id
            AND dc.warehouse_id = p_warehouse_id
            AND dc.dc_date BETWEEN p_from_date AND p_to_date
        ), 0)::INTEGER AS out_qty
    FROM products p
    JOIN warehouses w ON w.warehouse_id = p_warehouse_id
    WHERE (p_product_id IS NULL OR p.product_id = p_product_id)
    AND p.product_id IN (
        SELECT DISTINCT product_id FROM opening_stocks WHERE warehouse_id = p_warehouse_id
    );
END;
$$ LANGUAGE plpgsql;
 */
}
