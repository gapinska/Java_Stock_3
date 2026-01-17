package com.stockmarket.domain;

public class Commodity extends Asset {

    // procentowy koszt magazynowania jednej jednostki surowca
    private final double storageCostRatePerUnit;

    public Commodity(String symbol, String name, double marketPrice, double storageCostRatePerUnit) {
        super(symbol, name, marketPrice);

        if (storageCostRatePerUnit < 0) {
            throw new IllegalArgumentException("Storage cost rate cannot be negative");
        }


        this.storageCostRatePerUnit = storageCostRatePerUnit;
    }

    public double getStorageCostRatePerUnit() {
        return storageCostRatePerUnit;
    }

    @Override
    public AssetType getType() {
        return AssetType.COMMODITY;
    }

    @Override
    public double calculateRealValue(double quantity) {
        double baseValue = getMarketPrice() * quantity;

        // całkowity procentowy koszt magazynowania zależny od wolumenu
        double totalRate = storageCostRatePerUnit * quantity;

        // Koszt magazynowania nie może przekroczyć 100% wartości surowca
        totalRate = Math.min(totalRate, 1.0);

        return baseValue * (1.0 - totalRate);
    }

    @Override
    public double calculateAcquisitionCost(double quantity) {
        return getMarketPrice() * quantity;
    }
}
