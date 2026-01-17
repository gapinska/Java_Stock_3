package com.stockmarket.logic;

import com.stockmarket.domain.*;
import com.stockmarket.exceptions.DataIntegrityException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Deque;
import java.util.Map;

public class PortfolioPersistence {

    public void save(Portfolio portfolio, Path file) {
        if (portfolio == null) throw new IllegalArgumentException("Portfolio cannot be null");
        if (file == null) throw new IllegalArgumentException("File cannot be null");

        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            bw.write("HEADER|CASH|" + portfolio.getCash());
            bw.newLine();

            for (Map.Entry<String, Position> entry : portfolio.getPositions().entrySet()) {
                Position pos = entry.getValue();
                Asset a = pos.getAsset();

                // ASSET|TYPE|SYMBOL|DECLARED_QTY|NAME|MARKET_PRICE
                double declaredQty = 0.0;
                Deque<PurchaseLot> lots = pos.getLots();
                for (PurchaseLot lot : lots) {
                    declaredQty += lot.getQuantity();
                }

                bw.write("ASSET|" + a.getType() + "|" + a.getSymbol() + "|" + declaredQty + "|" + a.getName() + "|" + a.getMarketPrice());
                bw.newLine();

                for (PurchaseLot lot : lots) {
                    bw.write("LOT|" + lot.getPurchaseDate() + "|" + lot.getQuantity() + "|" + lot.getUnitPrice());
                    bw.newLine();
                }
            }

        } catch (IOException e) {
            throw new DataIntegrityException("I/O error during save", e);
        }
    }

    public Portfolio load(Path file) {
        if (file == null) throw new IllegalArgumentException("File cannot be null");

        try (BufferedReader br = Files.newBufferedReader(file)) {

            String line = br.readLine();
            if (line == null) throw new DataIntegrityException("Empty file");

            String[] header = split(line, 3);
            if (!"HEADER".equals(header[0]) || !"CASH".equals(header[1])) {
                throw new DataIntegrityException("Invalid header line: " + line);
            }

            double cash = parseDouble(header[2], "cash");
            Portfolio portfolio = new Portfolio(cash);

            Asset currentAsset = null;
            String currentSymbol = null;
            double lotsQtySum = 0.0;
            Double expectedQty = null;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = split(line, -1);

                if ("ASSET".equals(parts[0])) {
                    // Walidacja poprzedniego aktywa (jeśli było w formacie z deklarowaną ilością)
                    if (currentAsset != null && expectedQty != null) {
                        if (Double.compare(lotsQtySum, expectedQty) != 0) {
                            throw new DataIntegrityException(
                                    "Lots quantity sum mismatch for " + currentSymbol + ": expected=" + expectedQty + ", actual=" + lotsQtySum
                            );
                        }
                    }

                    expectedQty = null;
                    if (parts.length == 6) {
                        expectedQty = parseDouble(parts[3], "declared quantity");
                    } else if (parts.length != 5) {
                        throw new DataIntegrityException("Invalid ASSET line length");
                    }

                    currentAsset = parseAsset(parts);
                    currentSymbol = currentAsset.getSymbol();
                    lotsQtySum = 0.0;

                    // upewnij się, że pozycja istnieje
                    if (portfolio.getPosition(currentSymbol) == null) {
                        portfolio.getPositions().put(currentSymbol, new Position(currentAsset));
                    }
                } else if ("LOT".equals(parts[0])) {
                    if (currentAsset == null) throw new DataIntegrityException("LOT without ASSET: " + line);
                    if (parts.length != 4) throw new DataIntegrityException("Invalid LOT line length: " + line);

                    LocalDate date = parseDate(parts[1], "lot date");
                    double qty = parseDouble(parts[2], "lot quantity");
                    double unitPrice = parseDouble(parts[3], "lot unit price");

                    if (qty <= 0) throw new DataIntegrityException("Invalid lot quantity: " + qty);

                    Position pos = portfolio.getPosition(currentSymbol);
                    if (pos == null) throw new DataIntegrityException("Missing position for symbol: " + currentSymbol);

                    pos.addLot(new PurchaseLot(date, unitPrice, qty));
                    lotsQtySum += qty;
                } else {
                    throw new DataIntegrityException("Unknown line type: " + line);
                }
            }

            // Walidacja ostatniego aktywa
            if (currentAsset != null && expectedQty != null) {
                if (Double.compare(lotsQtySum, expectedQty) != 0) {
                    throw new DataIntegrityException(
                            "Lots quantity sum mismatch for " + currentSymbol + ": expected=" + expectedQty + ", actual=" + lotsQtySum
                    );
                }
            }

            return portfolio;

        } catch (IOException e) {
            throw new DataIntegrityException("I/O error during load", e);
        }
    }

    private Asset parseAsset(String[] parts) {
        // ASSET|TYPE|SYMBOL|NAME|MARKET_PRICE (legacy)
        // ASSET|TYPE|SYMBOL|DECLARED_QTY|NAME|MARKET_PRICE (v2)
        if (parts.length != 5 && parts.length != 6) {
            throw new DataIntegrityException("Invalid ASSET line length");
        }

        AssetType type;
        try {
            type = AssetType.valueOf(parts[1]);
        } catch (IllegalArgumentException ex) {
            throw new DataIntegrityException("Invalid asset type: " + parts[1]);
        }

        String symbol = parts[2];
        String name;
        double marketPrice;
        if (parts.length == 5) {
            name = parts[3];
            marketPrice = parseDouble(parts[4], "market price");
        } else {
            name = parts[4];
            marketPrice = parseDouble(parts[5], "market price");
        }

        // Wymagane: różne klasy aktywów polimorficznie
        return switch (type) {
            case SHARE -> new Share(symbol, name, marketPrice);
            case COMMODITY ->
                    // dla uproszczenia: storageCostRatePerUnit = 0 przy odczycie
                    new Commodity(symbol, name, marketPrice, 0.0);
            case CURRENCY ->
                    // dla uproszczenia: spread = 0 przy odczycie
                    new Currency(symbol, name, marketPrice, 0.0);
        };
    }

    private String[] split(String line, int expected) {
        String[] parts = line.split("\\|");
        if (expected > 0 && parts.length != expected) {
            throw new DataIntegrityException("Invalid line format: " + line);
        }
        return parts;
    }

    private double parseDouble(String s, String field) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            throw new DataIntegrityException("Invalid number for " + field + ": " + s);
        }
    }

    private LocalDate parseDate(String s, String field) {
        try {
            return LocalDate.parse(s);
        } catch (Exception ex) {
            throw new DataIntegrityException("Invalid date for " + field + ": " + s);
        }
    }
}
