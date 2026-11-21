package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dining_tables")
public class DiningTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private Long tableId;

    @Column(name = "table_number", nullable = false, unique = true)
    private String tableNumber;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String status;

    public DiningTable() {
    }

    public DiningTable(String tableNumber, int capacity) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = "AVAILABLE";
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

