
package com.example.demo.dto;

import java.math.BigDecimal;

public class DashboardResponse {

    private long totalTables;

    private long activeOrders;

    private BigDecimal todayPayments;

    public DashboardResponse(
            long totalTables,
            long activeOrders,
            BigDecimal todayPayments) {

        this.totalTables = totalTables;
        this.activeOrders = activeOrders;
        this.todayPayments = todayPayments;
    }

    public long getTotalTables() {
        return totalTables;
    }

    public long getActiveOrders() {
        return activeOrders;
    }

    public BigDecimal getTodayPayments() {
        return todayPayments;
    }
}
