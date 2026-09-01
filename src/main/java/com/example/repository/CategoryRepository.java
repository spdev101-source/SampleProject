package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Long> {
boolean existsByCategoryname(String categoryname);
boolean existsByCategorynameAndIdNot(String name,Long id);
/*
 CREATE OR REPLACE FUNCTION get_stock_report(
    p_from_date DATE,
    p_to_date DATE,
    p_warehouse_id BIGINT
)
RETURNS TABLE (
    product_id BIGINT,
    product_name TEXT,
    warehouse_id BIGINT,
    warehouse_name TEXT,
    opening BIGINT,
    stock_in BIGINT,
    stock_out BIGINT,
    closing BIGINT
)
AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.product_id,
        p.product_name,
        w.warehouse_id,
        w.warehouse_name,

        COALESCE((
            SELECT SUM(os.quantity) FROM opening_stock os
            WHERE os.product_id = p.product_id
              AND os.warehouse_id = w.warehouse_id
              AND os.opening_date < p_from_date
        ), 0) AS opening,

        COALESCE((
            SELECT SUM(pi.quantity) FROM purchase_items pi
            INNER JOIN purchases pu ON pu.id = pi.purchase_id
            WHERE pi.product_id = p.product_id
              AND pu.warehouse_id = w.warehouse_id
              AND pu.purchase_date BETWEEN p_from_date AND p_to_date
        ), 0) AS stock_in,

        COALESCE((
            SELECT SUM(di.quantity) FROM dc_items di
            INNER JOIN delivery_challans dc ON dc.id = di.dc_id
            WHERE di.product_id = p.product_id
              AND dc.warehouse_id = w.warehouse_id
              AND dc.dc_date BETWEEN p_from_date AND p_to_date
        ), 0) AS stock_out,

        (
            COALESCE((SELECT SUM(os.quantity) FROM opening_stock os
                WHERE os.product_id = p.product_id AND os.warehouse_id = w.warehouse_id
                AND os.opening_date < p_from_date), 0)
            +
            COALESCE((SELECT SUM(pi.quantity) FROM purchase_items pi
                INNER JOIN purchases pu ON pu.id = pi.purchase_id
                WHERE pi.product_id = p.product_id AND pu.warehouse_id = w.warehouse_id
                AND pu.purchase_date BETWEEN p_from_date AND p_to_date), 0)
            -
            COALESCE((SELECT SUM(di.quantity) FROM dc_items di
                INNER JOIN delivery_challans dc ON dc.id = di.dc_id
                WHERE di.product_id = p.product_id AND dc.warehouse_id = w.warehouse_id
                AND dc.dc_date BETWEEN p_from_date AND p_to_date), 0)
        ) AS closing

    FROM products p
    JOIN warehouses w ON w.warehouse_id = p_warehouse_id
    WHERE p.product_id IN (
        SELECT DISTINCT product_id FROM opening_stock WHERE warehouse_id = p_warehouse_id
    );
END;
$$ LANGUAGE plpgsql;

SELECT * FROM get_stock_report('2026-08-01', '2026-08-31', 1);

package com.example.dto.response;

public interface StockReportProjection {
	Long getProductId();
	String getProductName();
	Long getWarehouseId();
	String getWarehouseName();
	Long getOpening();
	Long getStockIn();
	Long getStockOut();
	Long getClosing();
}
@Query(value = "SELECT * FROM get_stock_report(:fromDate, :toDate, :warehouseId)",
       nativeQuery = true)
List<StockReportProjection> callStockReport(@Param("fromDate") LocalDate fromDate,
                                             @Param("toDate") LocalDate toDate,
                                             @Param("warehouseId") Long warehouseId);
                                             
    List<StockReportProjection> getStockReport(LocalDate fromDate, LocalDate toDate, Long warehouseId);
    
    
    @Override
public List<StockReportProjection> getStockReport(LocalDate fromDate, LocalDate toDate, Long warehouseId) {

    if (fromDate == null || toDate == null) {
        throw new RuntimeException("fromDate and toDate are required");
    }
    if (fromDate.isAfter(toDate)) {
        throw new RuntimeException("fromDate cannot be after toDate");
    }
    if (warehouseId == null) {
        throw new RuntimeException("warehouseId is required");
    }

    return openingStockRepository.callStockReport(fromDate, toDate, warehouseId);
}

@GetMapping("/stock-report")
public List<StockReportProjection> getStockReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam Long warehouseId) {
    return openingStockService.getStockReport(fromDate, toDate, warehouseId);
}

GET /api/opening-stocks/stock-report?fromDate=2026-08-01&toDate=2026-08-31&warehouseId=1
 */
}
