package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.PurchaseLot;
import com.stockmarket.exceptions.InsufficientFundsException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Portfolio {

    private double cash;
    private final Map<String, Position> positions;

    public Portfolio(double initialCash) {
        if (initialCash < 0) {
            throw new IllegalArgumentException("Initial cash cannot be negative");
        }
        this.cash = initialCash;
        this.positions = new HashMap<>();
    }

    public double getCash() {
        return cash;
    }

    public Map<String, Position> getPositions() {
        return positions;
    }

    public int getHoldingsCount() {
        return positions.size();
    }

    public Position getPosition(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("Symbol cannot be null");
        return positions.get(symbol);
    }

    public double calculateAssetsRealValue() {
        double sum = 0.0;
        for (Map.Entry<String, Position> entry : positions.entrySet()) {
            Position p = entry.getValue();
            sum += p.getMarketValue();
        }
        return sum;
    }

    public double calculateTotalValue() {
        return cash + calculateAssetsRealValue();
    }

    public void buyAsset(Asset asset, double quantity) {
        buyAsset(asset, quantity, LocalDate.now(), asset.getMarketPrice());
    }

    public void buyAsset(Asset asset, double quantity, LocalDate date, double unitPrice) {
        if (asset == null) throw new IllegalArgumentException("Asset cannot be null");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");

        double cost = asset.calculateAcquisitionCost(quantity);
        if (cost > cash) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        Position position = positions.get(asset.getSymbol());
        if (position == null) {
            position = new Position(asset);
            positions.put(asset.getSymbol(), position);
        }

        position.addLot(new PurchaseLot(date, unitPrice, quantity));
        cash -= cost;
    }

    public SaleResult sellAsset(String symbol, double quantity, double sellUnitPrice, LocalDate saleDate) {
        Position position = positions.get(symbol);
        if (position == null) {
            throw new IllegalArgumentException("No such asset in portfolio");
        }

        SaleResult result = position.sellFifo(quantity, sellUnitPrice);
        cash += quantity * sellUnitPrice;

        if (position.getTotalQuantity() <= 0.0) {
            positions.remove(symbol);
        }

        return result;
    }
}
