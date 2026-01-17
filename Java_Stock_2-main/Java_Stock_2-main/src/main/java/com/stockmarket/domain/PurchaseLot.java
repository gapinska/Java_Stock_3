package com.stockmarket.domain;

import java.time.LocalDate;

public class PurchaseLot { //zakup
    private final LocalDate purchaseDate;
    private final double unitPrice;
    private double quantity; //ile jeszcze zostało

    public PurchaseLot(LocalDate purchaseDate, double unitPrice, double quantity) {
        if (purchaseDate == null) {
            throw new NullPointerException("purchase date cannot be null");
        }

        if (unitPrice < 0) {
            throw new IllegalArgumentException("unit price cannot be negative");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException("quantity cannot be negative");
        }

        this.purchaseDate = purchaseDate;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getQuantity() {
        return quantity;
    }

    public void reduceQuantity(double amount) {
        if (amount <= 0 || amount > quantity) {
            throw new IllegalArgumentException("Invalid amount");
        }
        this.quantity -= amount;
    }
}