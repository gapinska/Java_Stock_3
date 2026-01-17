package com.stockmarket.logic;

import java.time.LocalDate;

public class SaleLine {
    private final LocalDate lotDate;
    private final double soldQuantity;
    private final double buyUnitPrice;
    private final double sellUnitPrice;
    private final double profit;

    public SaleLine(LocalDate lotDate, double soldQuantity, double buyUnitPrice, double sellUnitPrice) {
        this.lotDate = lotDate;
        this.soldQuantity = soldQuantity;
        this.buyUnitPrice = buyUnitPrice;
        this.sellUnitPrice = sellUnitPrice;
        this.profit = soldQuantity * (sellUnitPrice - buyUnitPrice);
    }

    public LocalDate getLotDate() { return lotDate; }
    public double getSoldQuantity() { return soldQuantity; }
    public double getBuyUnitPrice() { return buyUnitPrice; }
    public double getSellUnitPrice() { return sellUnitPrice; }
    public double getProfit() { return profit; }
}