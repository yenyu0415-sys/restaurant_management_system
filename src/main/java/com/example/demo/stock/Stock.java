package com.example.demo.stock;

public class Stock {

    private String id;

    private String name;

    private Double price;

    //  Constructor
    public Stock(String id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    //  getters & setters
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
