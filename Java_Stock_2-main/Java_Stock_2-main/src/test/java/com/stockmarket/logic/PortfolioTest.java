package com.stockmarket.logic;

import com.stockmarket.domain.Share;
import com.stockmarket.domain.Commodity;
import com.stockmarket.domain.Currency;
import com.stockmarket.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

    @Test
    void portfolioShouldBuyDifferentAssetTypes() {
        Portfolio portfolio = new Portfolio(2000.0);

        Share share = new Share("AAPL", "Apple", 100.0);
        Commodity commodity = new Commodity("GOLD", "Gold", 100.0, 0.01);
        Currency currency = new Currency("EUR", "Euro", 100.0, 5.0);

        portfolio.buyAsset(share, 2);      // koszt: 205
        portfolio.buyAsset(commodity, 2);  // koszt: 200
        portfolio.buyAsset(currency, 2);   // koszt: 200

        assertEquals(3, portfolio.getHoldingsCount());
        assertTrue(portfolio.getCash() < 2000.0);
    }

    @Test
    void portfolioAuditShouldUseRealValueForCommodity() {
        Portfolio portfolio = new Portfolio(1000.0);

        Commodity commodity = new Commodity("OIL", "Oil", 100.0, 0.01);

        portfolio.buyAsset(commodity, 10);

        double assetsValue = portfolio.calculateAssetsRealValue();

        // czysta wartość rynkowa = 100 * 10 = 1000
        // realna wartość musi być mniejsza (koszt magazynowania)
        assertTrue(assetsValue < 1000.0);
    }

    @Test
    void buyingAssetWithoutEnoughCashShouldThrowException() {
        Portfolio portfolio = new Portfolio(10.0);
        Share share = new Share("AAPL", "Apple", 10.0);

        assertThrows(InsufficientFundsException.class,
                () -> portfolio.buyAsset(share, 1));
    }
}
