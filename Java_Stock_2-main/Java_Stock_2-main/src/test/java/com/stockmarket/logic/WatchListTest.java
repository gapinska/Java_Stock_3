package com.stockmarket.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WatchListTest {

    @Test
    void watchListShouldGuaranteeUniquenessByStructure() {
        WatchList wl = new WatchList();

        assertTrue(wl.add("AAPL"));
        assertFalse(wl.add("AAPL"));
        assertEquals(1, wl.size());
        assertTrue(wl.contains("AAPL"));
    }
}

