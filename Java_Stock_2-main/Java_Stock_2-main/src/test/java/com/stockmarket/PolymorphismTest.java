package com.stockmarket;

import com.stockmarket.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PolymorphismTest {

    @Test
    void sameMarketPriceAndQuantityShouldProduceDifferentRealValues() {
        double marketPrice = 100.0;
        double quantity = 10.0;

        Asset share = new Share("AAPL", "Apple", marketPrice);
        Asset commodity = new Commodity("GOLD", "Gold", marketPrice, 0.001);
        Asset currency = new Currency("EUR", "Euro", marketPrice, 5.0);

        double v1 = share.calculateRealValue(quantity);
        double v2 = commodity.calculateRealValue(quantity);
        double v3 = currency.calculateRealValue(quantity);

        assertNotEquals(v1, v2, 0.0001);
        assertNotEquals(v1, v3, 0.0001);
        assertNotEquals(v2, v3, 0.0001);
    }
}
