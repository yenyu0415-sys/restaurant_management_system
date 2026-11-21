package com.example.demo.test;

public class StockUpdateRequest {

    private String oldStock;
    private String newStock;

    public String getOldStock() {
        return oldStock;
    }

    public String getNewStock() {
        return newStock;
    }

    public void setOldStock(String oldStock) {
        this.oldStock = oldStock;
    }

    public void setNewStock(String newStock) {
        this.newStock = newStock;
    }
}