package com.example.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String categoryname;
    private String description;
    private Boolean isactive = true;
    private LocalDateTime createdat;
    private LocalDateTime updatedat;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCategoryname() {
		return categoryname;
	}
	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getIsactive() {
		return isactive;
	}
	public void setIsactive(Boolean isactive) {
		this.isactive = isactive;
	}
	public LocalDateTime getCreatedat() {
		return createdat;
	}
	public void setCreatedat(LocalDateTime createdat) {
		this.createdat = createdat;
	}
	public LocalDateTime getUpdatedat() {
		return updatedat;
	}
	public void setUpdatedat(LocalDateTime updatedat) {
		this.updatedat = updatedat;
	}
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
 
	//==================================================
	/--3. Unit Price (latest purchase price)
	SELECT pi2.unit_price 
FROM purchase_items pi2
INNER JOIN purchases pu2 ON pu2.id = pi2.purchase_id
WHERE pi2.product_id = 1 
  AND pu2.warehouse_id = 1
ORDER BY pu2.purchase_date DESC
LIMIT 1;

/--4. Opening Stock
 SELECT COALESCE(SUM(quantity), 0) AS opening
FROM opening_stock
WHERE product_id = 1 
  AND warehouse_id = 1
  AND opening_date < '2026-08-01';
  
  /--5. Stock IN — Part A: Purchases
   SELECT COALESCE(SUM(pi.quantity), 0) AS purchase_qty
FROM purchase_items pi
INNER JOIN purchases pu ON pu.id = pi.purchase_id
WHERE pi.product_id = 1 
  AND pu.warehouse_id = 1
  AND pu.purchase_date BETWEEN '2026-08-01' AND '2026-08-31';
  
  /--6. Stock IN — Part B: Sales Returns
   SELECT COALESCE(SUM(sri.quantity), 0) AS sales_return_qty
FROM sales_return_items sri
INNER JOIN sales_returns sr ON sr.return_id = sri.return_id
WHERE sri.product_id = 1 
  AND sr.warehouse_id = 1
  AND sr.return_date BETWEEN '2026-08-01' AND '2026-08-31';
  
  /--7. Stock OUT — Part A: Delivery Challans
   SELECT COALESCE(SUM(di.quantity), 0) AS delivery_qty
FROM dc_items di
INNER JOIN delivery_challans dc ON dc.id = di.dc_id
WHERE di.product_id = 1 
  AND dc.warehouse_id = 1
  AND dc.dc_date BETWEEN '2026-08-01' AND '2026-08-31';
  
  /--8. Stock OUT — Part B: Purchase Returns
   * SELECT COALESCE(SUM(pri.quantity), 0) AS purchase_return_qty
FROM purchase_return_items pri
INNER JOIN purchase_returns pr ON pr.return_id = pri.return_id
WHERE pri.product_id = 1 
  AND pr.warehouse_id = 1
  AND pr.return_date BETWEEN '2026-08-01' AND '2026-08-31';
  /=========stock in combined
   SELECT 
    COALESCE((
        SELECT SUM(pi.quantity) FROM purchase_items pi
        INNER JOIN purchases pu ON pu.id = pi.purchase_id
        WHERE pi.product_id = 1
          AND pu.warehouse_id = 1
          AND pu.purchase_date BETWEEN '2026-08-01' AND '2026-08-31'
    ), 0)
    +
    COALESCE((
        SELECT SUM(sri.quantity) FROM sales_return_items sri
        INNER JOIN sales_returns sr ON sr.return_id = sri.return_id
        WHERE sri.product_id = 1
          AND sr.warehouse_id = 1
          AND sr.return_date BETWEEN '2026-08-01' AND '2026-08-31'
    ), 0) AS stock_in;
    
    =========STOCK OUT COMBINED
    SELECT 
    COALESCE((
        SELECT SUM(di.quantity) FROM dc_items di
        INNER JOIN delivery_challans dc ON dc.id = di.dc_id
        WHERE di.product_id = 1
          AND dc.warehouse_id = 1
          AND dc.dc_date BETWEEN '2026-08-01' AND '2026-08-31'
    ), 0)
    +
    COALESCE((
        SELECT SUM(pri.quantity) FROM purchase_return_items pri
        INNER JOIN purchase_returns pr ON pr.return_id = pri.return_id
        WHERE pri.product_id = 1
          AND pr.warehouse_id = 1
          AND pr.return_date BETWEEN '2026-08-01' AND '2026-08-31'
    ), 0) AS stock_out;
    =================CLOSING==========
    SELECT
    (
        COALESCE((SELECT SUM(os.quantity) FROM opening_stock os
            WHERE os.product_id = 1 AND os.warehouse_id = 1
            AND os.opening_date < '2026-08-01'), 0)
        +
        (
            COALESCE((SELECT SUM(pi.quantity) FROM purchase_items pi
                INNER JOIN purchases pu ON pu.id = pi.purchase_id
                WHERE pi.product_id = 1 AND pu.warehouse_id = 1
                AND pu.purchase_date BETWEEN '2026-08-01' AND '2026-08-31'), 0)
            +
            COALESCE((SELECT SUM(sri.quantity) FROM sales_return_items sri
                INNER JOIN sales_returns sr ON sr.return_id = sri.return_id
                WHERE sri.product_id = 1 AND sr.warehouse_id = 1
                AND sr.return_date BETWEEN '2026-08-01' AND '2026-08-31'), 0)
        )
        -
        (
            COALESCE((SELECT SUM(di.quantity) FROM dc_items di
                INNER JOIN delivery_challans dc ON dc.id = di.dc_id
                WHERE di.product_id = 1 AND dc.warehouse_id = 1
                AND dc.dc_date BETWEEN '2026-08-01' AND '2026-08-31'), 0)
            +
            COALESCE((SELECT SUM(pri.quantity) FROM purchase_return_items pri
                INNER JOIN purchase_returns pr ON pr.return_id = pri.return_id
                WHERE pri.product_id = 1 AND pr.warehouse_id = 1
                AND pr.return_date BETWEEN '2026-08-01' AND '2026-08-31'), 0)
        )
    ) AS closing;
    
    ===================TOTAL AMOUNT==================
    SELECT
    COALESCE((
        SELECT pi3.unit_price FROM purchase_items pi3
        INNER JOIN purchases pu3 ON pu3.id = pi3.purchase_id
        WHERE pi3.product_id = 1
          AND pu3.warehouse_id = 1
        ORDER BY pu3.purchase_date DESC
        LIMIT 1
    ), 0)
    *
    (
        COALESCE((SELECT SUM(os.quantity) FROM opening_stock os
            WHERE os.product_id = 1 AND os.warehouse_id = 1
            AND os.opening_date < '2026-08-01'), 0)
        +
        (
            COALESCE((SELECT SUM(pi.quantity) FROM purchase_items pi
                INNER JOIN purchases pu ON pu.id = pi.purchase_id
                WHERE pi.product_id = 1 AND pu.warehouse_id = 1
                AND pu.purchase_date BETWEEN '2026-08-01' AND '2026-08-31'), 0)
            +
            COALESCE((SELECT SUM(sri.quantity) FROM sales_return_items sri
                INNER JOIN sales_returns sr ON sr.return_id = sri.return_id
                WHERE sri.product_id = 1 AND sr.warehouse_id = 1
                AND sr.return_date BETWEEN '2026-08-01' AND '2026-08-31'), 0)
        )
        -
        (
            COALESCE((SELECT SUM(di.quantity) FROM dc_items di
                INNER JOIN delivery_challans dc ON dc.id = di.dc_id
                WHERE di.product_id = 1 AND dc.warehouse_id = 1
                AND dc.dc_date BETWEEN '2026-08-01' AND '2026-08-31'), 0)
            +
            COALESCE((SELECT SUM(pri.quantity) FROM purchase_return_items pri
                INNER JOIN purchase_returns pr ON pr.return_id = pri.return_id
                WHERE pri.product_id = 1 AND pr.warehouse_id = 1
                AND pr.return_date BETWEEN '2026-08-01' AND '2026-08-31'), 0)
        )
    ) AS total_amount;
    
    //=============================================================
    ///
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
    unit_price NUMERIC,
    opening BIGINT,
    stock_in BIGINT,
    stock_out BIGINT,
    closing BIGINT,
    total_amount NUMERIC
)
AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.product_id,
        p.product_name,
        w.warehouse_id,
        w.warehouse_name,

        (
            SELECT pi2.unit_price FROM purchase_items pi2
            INNER JOIN purchases pu2 ON pu2.id = pi2.purchase_id
            WHERE pi2.product_id = p.product_id
              AND pu2.warehouse_id = w.warehouse_id
            ORDER BY pu2.purchase_date DESC
            LIMIT 1
        ) AS unit_price,

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
        ), 0)
        +
        COALESCE((
            SELECT SUM(sri.quantity) FROM sales_return_items sri
            INNER JOIN sales_returns sr ON sr.return_id = sri.return_id
            WHERE sri.product_id = p.product_id
              AND sr.warehouse_id = w.warehouse_id
              AND sr.return_date BETWEEN p_from_date AND p_to_date
        ), 0) AS stock_in,

        COALESCE((
            SELECT SUM(di.quantity) FROM dc_items di
            INNER JOIN delivery_challans dc ON dc.id = di.dc_id
            WHERE di.product_id = p.product_id
              AND dc.warehouse_id = w.warehouse_id
              AND dc.dc_date BETWEEN p_from_date AND p_to_date
        ), 0)
        +
        COALESCE((
            SELECT SUM(pri.quantity) FROM purchase_return_items pri
            INNER JOIN purchase_returns pr ON pr.return_id = pri.return_id
            WHERE pri.product_id = p.product_id
              AND pr.warehouse_id = w.warehouse_id
              AND pr.return_date BETWEEN p_from_date AND p_to_date
        ), 0) AS stock_out,

        (
            COALESCE((SELECT SUM(os.quantity) FROM opening_stock os
                WHERE os.product_id = p.product_id AND os.warehouse_id = w.warehouse_id
                AND os.opening_date < p_from_date), 0)
            +
            (
                COALESCE((SELECT SUM(pi.quantity) FROM purchase_items pi
                    INNER JOIN purchases pu ON pu.id = pi.purchase_id
                    WHERE pi.product_id = p.product_id AND pu.warehouse_id = w.warehouse_id
                    AND pu.purchase_date BETWEEN p_from_date AND p_to_date), 0)
                +
                COALESCE((SELECT SUM(sri.quantity) FROM sales_return_items sri
                    INNER JOIN sales_returns sr ON sr.return_id = sri.return_id
                    WHERE sri.product_id = p.product_id AND sr.warehouse_id = w.warehouse_id
                    AND sr.return_date BETWEEN p_from_date AND p_to_date), 0)
            )
            -
            (
                COALESCE((SELECT SUM(di.quantity) FROM dc_items di
                    INNER JOIN delivery_challans dc ON dc.id = di.dc_id
                    WHERE di.product_id = p.product_id AND dc.warehouse_id = w.warehouse_id
                    AND dc.dc_date BETWEEN p_from_date AND p_to_date), 0)
                +
                COALESCE((SELECT SUM(pri.quantity) FROM purchase_return_items pri
                    INNER JOIN purchase_returns pr ON pr.return_id = pri.return_id
                    WHERE pri.product_id = p.product_id AND pr.warehouse_id = w.warehouse_id
                    AND pr.return_date BETWEEN p_from_date AND p_to_date), 0)
            )
        ) AS closing,

        COALESCE((
            SELECT pi3.unit_price FROM purchase_items pi3
            INNER JOIN purchases pu3 ON pu3.id = pi3.purchase_id
            WHERE pi3.product_id = p.product_id
              AND pu3.warehouse_id = w.warehouse_id
            ORDER BY pu3.purchase_date DESC
            LIMIT 1
        ), 0)
        *
        (
            COALESCE((SELECT SUM(os.quantity) FROM opening_stock os
                WHERE os.product_id = p.product_id AND os.warehouse_id = w.warehouse_id
                AND os.opening_date < p_from_date), 0)
            +
            (
                COALESCE((SELECT SUM(pi.quantity) FROM purchase_items pi
                    INNER JOIN purchases pu ON pu.id = pi.purchase_id
                    WHERE pi.product_id = p.product_id AND pu.warehouse_id = w.warehouse_id
                    AND pu.purchase_date BETWEEN p_from_date AND p_to_date), 0)
                +
                COALESCE((SELECT SUM(sri.quantity) FROM sales_return_items sri
                    INNER JOIN sales_returns sr ON sr.return_id = sri.return_id
                    WHERE sri.product_id = p.product_id AND sr.warehouse_id = w.warehouse_id
                    AND sr.return_date BETWEEN p_from_date AND p_to_date), 0)
            )
            -
            (
                COALESCE((SELECT SUM(di.quantity) FROM dc_items di
                    INNER JOIN delivery_challans dc ON dc.id = di.dc_id
                    WHERE di.product_id = p.product_id AND dc.warehouse_id = w.warehouse_id
                    AND dc.dc_date BETWEEN p_from_date AND p_to_date), 0)
                +
                COALESCE((SELECT SUM(pri.quantity) FROM purchase_return_items pri
                    INNER JOIN purchase_returns pr ON pr.return_id = pri.return_id
                    WHERE pri.product_id = p.product_id AND pr.warehouse_id = w.warehouse_id
                    AND pr.return_date BETWEEN p_from_date AND p_to_date), 0)
            )
        ) AS total_amount

    FROM products p
    JOIN warehouses w ON w.warehouse_id = p_warehouse_id
    WHERE p.product_id IN (
        SELECT DISTINCT product_id FROM opening_stock WHERE warehouse_id = p_warehouse_id
    );
END;
$$ LANGUAGE plpgsql;

///https://claude.ai/share/da6b6f74-66f9-4d7a-b82b-7c737ee9db61
package com.example.dto.response;

import java.math.BigDecimal;

public interface StockReportProjection {
	Long getProductId();
	String getProductName();
	Long getWarehouseId();
	String getWarehouseName();
	BigDecimal getUnitPrice();
	Long getOpening();
	Long getStockIn();
	Long getStockOut();
	Long getClosing();
	BigDecimal getTotalAmount();
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
