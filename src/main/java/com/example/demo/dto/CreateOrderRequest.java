package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class CreateOrderRequest {

    @NotNull(message = "Session ID is required")
    @Positive(message = "Session ID must be greater than zero")
    private Long sessionId;

    @NotNull(message = "Employee ID is required")
    @Positive(message = "Employee ID must be greater than zero")
    private Long employeeId;

    @NotEmpty(message = "Order items cannot be empty")
    @Valid

    private List<OrderItemRequest> items;

    public CreateOrderRequest() {
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
