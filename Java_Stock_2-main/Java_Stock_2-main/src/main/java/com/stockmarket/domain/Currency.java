package com.stockmarket.domain;

public class Currency extends Asset {
    private final double spread;

    public Currency(String symbol, String name, double marketPrice, double spread) {
        super(symbol, name, marketPrice);

        if (spread < 0) {
            throw new IllegalArgumentException("Spread cannot be negative");
        }

        this.spread = spread;
    }

    public double getSpread() {
        return spread;
    }

    @Override
    public AssetType getType() {
        return AssetType.CURRENCY;
    }

    @Override
    public double calculateRealValue(double quantity) {
        //bidPrice = cena, jaką otrzymasz, gdy sprzedajesz walutę
        double bidPrice = getMarketPrice() - spread;
        if (bidPrice < 0) {
            bidPrice = 0;
        }
        return bidPrice * quantity;
    }

    @Override
    public double calculateAcquisitionCost(double quantity) {
        return getMarketPrice() * quantity;
    }
}
