package com.stockmarket.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseLotTest {

    @Test
    void constructorShouldValidateArguments() {
        assertThrows(NullPointerException.class, () -> new PurchaseLot(null, 1.0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new PurchaseLot(LocalDate.now(), -1.0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new PurchaseLot(LocalDate.now(), 1.0, -1.0));
    }

    @Test
    void reduceQuantityShouldValidateAndReduce() {
        PurchaseLot lot = new PurchaseLot(LocalDate.parse("2023-01-01"), 100.0, 10.0);

        assertThrows(IllegalArgumentException.class, () -> lot.reduceQuantity(0.0));
        assertThrows(IllegalArgumentException.class, () -> lot.reduceQuantity(-1.0));
        assertThrows(IllegalArgumentException.class, () -> lot.reduceQuantity(11.0));

        lot.reduceQuantity(3.0);
        assertEquals(7.0, lot.getQuantity(), 0.0001);
        assertEquals(100.0, lot.getUnitPrice(), 0.0001);
        assertEquals(LocalDate.parse("2023-01-01"), lot.getPurchaseDate());
    }
}

