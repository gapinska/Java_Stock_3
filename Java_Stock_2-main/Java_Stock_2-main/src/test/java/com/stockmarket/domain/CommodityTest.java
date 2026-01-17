package com.stockmarket.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommodityTest {

    @Test
    void commodityShouldLoseValueWithHigherQuantity() {
        Commodity commodity = new Commodity("GOLD", "Gold", 100.0, 0.001);

        double valueSmall = commodity.calculateRealValue(1);
        double valueLarge = commodity.calculateRealValue(100);

        double unitLarge = valueLarge / 100;

        assertTrue(unitLarge < valueSmall);
    }

    @Test
    void commodityValueShouldNeverBeNegative() {
        Commodity commodity = new Commodity("OIL", "Oil", 100.0, 0.1);

        double value = commodity.calculateRealValue(1000);

        assertEquals(0.0, value, 0.0001);
    }
}