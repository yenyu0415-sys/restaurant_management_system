
package com.example.demo.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private String menuName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public OrderItemResponse(
            String menuName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal) {

        this.menuName = menuName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public String getMenuName() {
        return menuName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
