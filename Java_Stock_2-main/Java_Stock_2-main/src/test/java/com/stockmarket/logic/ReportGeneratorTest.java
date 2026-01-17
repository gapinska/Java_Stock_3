package com.stockmarket.logic;

import com.stockmarket.domain.Commodity;
import com.stockmarket.domain.Currency;
import com.stockmarket.domain.PurchaseLot;
import com.stockmarket.domain.Share;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReportGeneratorTest {

    @Test
    void reportShouldSortByTypeThenMarketValueDescending() {
        Portfolio portfolio = new Portfolio(0.0);

        Share bigShare = new Share("AAA", "AAA", 10.0);
        Share smallShare = new Share("BBB", "BBB", 10.0);
        Commodity commodity = new Commodity("GOLD", "Gold", 10.0, 0.0);
        Currency currency = new Currency("EUR", "Euro", 10.0, 1.0);

        Position p1 = new Position(bigShare);
        p1.addLot(new PurchaseLot(LocalDate.parse("2023-01-01"), 10.0, 10.0)); // value 100
        portfolio.getPositions().put("AAA", p1);

        Position p2 = new Position(smallShare);
        p2.addLot(new PurchaseLot(LocalDate.parse("2023-01-01"), 10.0, 2.0)); // value 20
        portfolio.getPositions().put("BBB", p2);

        Position p3 = new Position(commodity);
        p3.addLot(new PurchaseLot(LocalDate.parse("2023-01-01"), 10.0, 1.0));
        portfolio.getPositions().put("GOLD", p3);

        Position p4 = new Position(currency);
        p4.addLot(new PurchaseLot(LocalDate.parse("2023-01-01"), 10.0, 1.0));
        portfolio.getPositions().put("EUR", p4);

        String report = new ReportGenerator().generate(portfolio);

        int idxShareAAA = report.indexOf("SHARE AAA");
        int idxShareBBB = report.indexOf("SHARE BBB");
        int idxCommodity = report.indexOf("COMMODITY GOLD");
        int idxCurrency = report.indexOf("CURRENCY EUR");

        assertTrue(idxShareAAA >= 0);
        assertTrue(idxShareBBB >= 0);
        assertTrue(idxCommodity >= 0);
        assertTrue(idxCurrency >= 0);

        // typ: SHARE -> COMMODITY -> CURRENCY (kolejność enum)
        assertTrue(idxShareAAA < idxCommodity);
        assertTrue(idxCommodity < idxCurrency);

        // w ramach SHARE: wartość malejąco, więc AAA (100) przed BBB (20)
        assertTrue(idxShareAAA < idxShareBBB);
    }
}

