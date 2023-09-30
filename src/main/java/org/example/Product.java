package org.example;

public class Product {
    private final String name;
    private final String quantity;

    public Product(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }
}
