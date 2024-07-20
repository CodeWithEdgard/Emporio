package com.loja.models;

import java.time.LocalDate;

public class Product {
    private int id;
    private String name;
    private String category;
    private String supplier;
    private LocalDate expirationDate;
    private LocalDate purchaseDate;
    private int quantity;

    public Product(int id, String name, String category, String supplier, LocalDate expirationDate, LocalDate purchaseDate, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.supplier = supplier;
        this.expirationDate = expirationDate;
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
