package com.stockmarket.logic;

public class SaleResult {

    private final String symbol;
    private final double totalSoldQuantity;
    private final SaleLine[] lines;
    private final int linesCount;
    private final double totalProfit;

    public SaleResult(String symbol, double totalSoldQuantity, SaleLine[] lines, int linesCount, double totalProfit) {
        this.symbol = symbol;
        this.totalSoldQuantity = totalSoldQuantity;
        this.lines = lines;
        this.linesCount = linesCount;
        this.totalProfit = totalProfit;
    }

    public String getSymbol() { return symbol; }
    public double getTotalSoldQuantity() { return totalSoldQuantity; }
    public int getLinesCount() { return linesCount; }
    public double getTotalProfit() { return totalProfit; }

    public SaleLine getLine(int index) {
        if (index < 0 || index >= linesCount) throw new IndexOutOfBoundsException();
        return lines[index];
    }
}