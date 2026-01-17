package com.stockmarket.domain;

import java.util.Objects;

public abstract class Asset {
    private final String symbol;
    private final String name;
    private final double marketPrice;

    public Asset(String symbol, String name, double marketPrice) {
        this.symbol = Objects.requireNonNull(symbol);
        this.name = Objects.requireNonNull(name);

        if (marketPrice < 0) {
            throw new IllegalArgumentException("Market price cannot be negative");
        }

        this.marketPrice = marketPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public abstract AssetType getType();

    public abstract double calculateRealValue(double quantity);
    public abstract double calculateAcquisitionCost(double quantity);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Asset other = (Asset) obj;
        return Objects.equals(this.symbol, other.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}
