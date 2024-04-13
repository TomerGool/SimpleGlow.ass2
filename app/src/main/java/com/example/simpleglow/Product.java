package com.example.simpleglow;
public class Product {
    private long id;
    private String name;
    private int quantity;
    private String category; // New property for product category

    public Product(String name, int quantity, String category) {
        this.name = name;
        this.quantity = quantity;
        this.category = category;
    }

    // Getter methods

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory(){
        return category;
    }
}
