package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.AssetType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    public String generate(Portfolio portfolio) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PORTFOLIO REPORT ===\n");
        sb.append("Cash: ").append(portfolio.getCash()).append("\n");

        List<Position> list = new ArrayList<>();
        for (Map.Entry<String, Position> entry : portfolio.getPositions().entrySet()) {
            list.add(entry.getValue());
        }

        Collections.sort(list, new Comparator<Position>() {
            @Override
            public int compare(Position p1, Position p2) {
                Asset a1 = p1.getAsset();
                Asset a2 = p2.getAsset();

                AssetType t1 = a1.getType();
                AssetType t2 = a2.getType();
                int byType = t1.compareTo(t2);
                if (byType != 0) return byType;

                double v1 = p1.getMarketValue();
                double v2 = p2.getMarketValue();
                return Double.compare(v2, v1);
            }
        });

        sb.append("\n--- Positions ---\n");
        for (int i = 0; i < list.size(); i++) {
            Position p = list.get(i);
            sb.append(p.getAsset().getType()).append(" ")
                    .append(p.getAsset().getSymbol()).append(" ")
                    .append(p.getAsset().getName()).append(" | qty=")
                    .append(p.getTotalQuantity()).append(" | value=")
                    .append(p.getMarketValue()).append("\n");
        }

        sb.append("\nAssets value: ").append(portfolio.calculateAssetsRealValue()).append("\n");
        sb.append("Total value: ").append(portfolio.calculateTotalValue()).append("\n");

        return sb.toString();
    }
}