package com.stockmarket.logic;

public class Order {
    private final String symbol;
    private final OrderSide side;
    private final double limitPrice;
    private final double quantity;
    private final long createdAtMillis;

    public Order(String symbol, OrderSide side, double limitPrice, double quantity, long createdAtMillis) {
        if (symbol == null) throw new IllegalArgumentException("Symbol cannot be null");
        if (side == null) throw new IllegalArgumentException("Side cannot be null");
        if (limitPrice < 0) throw new IllegalArgumentException("Limit price cannot be negative");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");

        this.symbol = symbol;
        this.side = side;
        this.limitPrice = limitPrice;
        this.quantity = quantity;
        this.createdAtMillis = createdAtMillis;
    }

    public String getSymbol() { return symbol; }
    public OrderSide getSide() { return side; }
    public double getLimitPrice() { return limitPrice; }
    public double getQuantity() { return quantity; }
    public long getCreatedAtMillis() { return createdAtMillis; }
}