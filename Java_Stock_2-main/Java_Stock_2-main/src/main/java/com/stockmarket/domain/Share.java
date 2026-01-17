package com.stockmarket.domain;

public class Share extends Asset {
    private static final double MANIPULATION_FEE = 5.0;

    public Share(String symbol, String name, double marketPrice) {
        super(symbol, name, marketPrice);
    }

    @Override
    public AssetType getType() {
        return AssetType.SHARE;
    }

    @Override
    public double calculateRealValue(double quantity) {
        return getMarketPrice() * quantity;
    }

    @Override
    public double calculateAcquisitionCost(double quantity) {
        return getMarketPrice() * quantity + MANIPULATION_FEE;
    }
}
