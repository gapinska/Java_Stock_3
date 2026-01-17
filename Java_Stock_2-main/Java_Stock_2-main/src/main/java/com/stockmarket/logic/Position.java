package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.PurchaseLot;
import com.stockmarket.exceptions.InsufficientAssetQuantityException;

import java.util.ArrayDeque;
import java.util.Deque;

public class Position {

    private final Asset asset;
    private final Deque<PurchaseLot> lots; // FIFO: najstarszy na początku

    public Position(Asset asset) {
        if (asset == null) throw new IllegalArgumentException("Asset cannot be null");
        this.asset = asset;
        this.lots = new ArrayDeque<>();
    }

    public Asset getAsset() {
        return asset;
    }

    public void addLot(PurchaseLot lot) {
        if (lot == null) throw new IllegalArgumentException("Lot cannot be null");
        lots.addLast(lot);
    }

    public double getTotalQuantity() {
        double sum = 0.0;
        for (PurchaseLot lot : lots) {
            sum += lot.getQuantity();
        }
        return sum;
    }

    public double getMarketValue() {
        return asset.calculateRealValue(getTotalQuantity());
    }

    public SaleResult sellFifo(double quantityToSell, double sellUnitPrice) {
        if (quantityToSell <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (sellUnitPrice < 0) throw new IllegalArgumentException("Sell price cannot be negative");

        double available = getTotalQuantity();
        if (quantityToSell > available) {
            throw new InsufficientAssetQuantityException(
                    "Not enough quantity to sell. Requested=" + quantityToSell + ", available=" + available
            );
        }

        // maksimum: jedna linia raportu na jedną partię (lot), bo sprzedajemy FIFO bez przeplatania
        SaleLine[] lines = new SaleLine[lots.size()];
        int linesCount = 0;

        double remaining = quantityToSell;
        double totalProfit = 0.0;

        while (remaining > 0) {
            PurchaseLot lot = lots.peekFirst(); // najstarszy

            double fromLot = Math.min(remaining, lot.getQuantity());

            // raport
            SaleLine line = new SaleLine(lot.getPurchaseDate(), fromLot, lot.getUnitPrice(), sellUnitPrice);
            lines[linesCount++] = line;
            totalProfit += line.getProfit();

            // zużycie partii
            lot.reduceQuantity(fromLot);
            remaining -= fromLot;

            // jeśli partia pusta, zdejmujemy ją z kolejki
            if (lot.getQuantity() <= 0.0) {
                lots.removeFirst();
            }
        }

        return new SaleResult(asset.getSymbol(), quantityToSell, lines, linesCount, totalProfit);
    }

    // Potrzebne do persystencji
    public Deque<PurchaseLot> getLots() {
        return lots;
    }
}
