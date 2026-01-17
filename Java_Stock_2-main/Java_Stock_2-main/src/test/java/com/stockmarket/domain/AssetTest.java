package com.stockmarket.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssetTest {

    @Test
    void assetConstructorShouldRejectNullSymbol() {
        assertThrows(NullPointerException.class, () -> new Share(null, "Apple", 10.0));
    }

    @Test
    void assetConstructorShouldRejectNullName() {
        assertThrows(NullPointerException.class, () -> new Share("AAPL", null, 10.0));
    }

    @Test
    void assetConstructorShouldRejectNegativeMarketPrice() {
        assertThrows(IllegalArgumentException.class, () -> new Share("AAPL", "Apple", -1.0));
    }

    @Test
    void equalsAndHashCodeShouldUseSymbolWithinSameClass() {
        Share a1 = new Share("AAPL", "Apple1", 10.0);
        Share a2 = new Share("AAPL", "Apple2", 99.0);
        Share b = new Share("MSFT", "Microsoft", 10.0);

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1, b);
        assertNotEquals(a1, null);
        assertEquals(a1, a1);
    }

    @Test
    void equalsShouldBeFalseForDifferentAssetClassesEvenWithSameSymbol() {
        Share share = new Share("AAA", "AAA", 10.0);
        Commodity commodity = new Commodity("AAA", "AAA", 10.0, 0.0);

        assertNotEquals(share, commodity);
    }

    @Test
    void enumsShouldBeAvailable() {
        assertEquals(3, AssetType.values().length);
    }
}
