package com.stockmarket.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShareTest {
    @Test
    void realValueShouldBePureMarketPriceTimesQuantity() {
        Share share = new Share("AAPL", "Apple", 100.0);

        assertEquals(1000.0, share.calculateRealValue(10), 0.0001);
    }

    @Test
    void acquisitionCostShouldIncludeManipulationFee() {
        Share share = new Share("AAPL", "Apple", 100.0);

        assertEquals(1005.0, share.calculateAcquisitionCost(10), 0.0001);
    }
}
