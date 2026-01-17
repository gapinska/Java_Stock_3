package com.stockmarket.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    @Test
    void currencyValueShouldUseBidPrice() {
        Currency currency = new Currency("EUR", "Euro", 100.0, 2.0);

        double value = currency.calculateRealValue(10);

        assertEquals(980.0, value, 0.0001);
    }

    @Test
    void currencyShouldAlwaysBeWorthLessThanMarketValue() {
        Currency currency = new Currency("USD", "US Dollar", 100.0, 2.0);

        double value = currency.calculateRealValue(10);

        assertTrue(value < 100.0 * 10);
    }
}