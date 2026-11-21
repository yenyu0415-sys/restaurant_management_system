
package com.example.demo.dto;

import com.example.demo.entity.OrderStatus;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long orderId;
    private Long sessionId;
    private Long employeeId;
    private LocalDateTime orderTime;
    private OrderStatus status;

    private List<OrderItemResponse> items;

    private BigDecimal totalAmount;

    public OrderResponse(
            Long orderId,
            Long sessionId,
            Long employeeId,
            LocalDateTime orderTime,
            OrderStatus status,
            List<OrderItemResponse> items,
            BigDecimal totalAmount) {

        this.orderId = orderId;
        this.sessionId = sessionId;
        this.employeeId = employeeId;
        this.orderTime = orderTime;
        this.status = status;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
