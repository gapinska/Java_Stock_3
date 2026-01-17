package com.stockmarket.logic;

import com.stockmarket.domain.PurchaseLot;
import com.stockmarket.domain.Share;
import com.stockmarket.exceptions.InsufficientAssetQuantityException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PositionFifoSellTest {

    @Test
    void sellingAcrossMultipleLotsShouldUseFifoAndProducePrecisePnLReport() {
        Share share = new Share("XYZ", "XYZ Corp", 1.0);
        Position position = new Position(share);

        position.addLot(new PurchaseLot(LocalDate.parse("2023-01-01"), 100.0, 10.0));
        position.addLot(new PurchaseLot(LocalDate.parse("2023-02-01"), 120.0, 10.0));

        SaleResult result = position.sellFifo(15.0, 150.0);

        assertEquals("XYZ", result.getSymbol());
        assertEquals(15.0, result.getTotalSoldQuantity(), 0.0001);
        assertEquals(2, result.getLinesCount());
        assertEquals(650.0, result.getTotalProfit(), 0.0001);

        SaleLine l1 = result.getLine(0);
        assertEquals(LocalDate.parse("2023-01-01"), l1.getLotDate());
        assertEquals(10.0, l1.getSoldQuantity(), 0.0001);
        assertEquals(100.0, l1.getBuyUnitPrice(), 0.0001);
        assertEquals(150.0, l1.getSellUnitPrice(), 0.0001);
        assertEquals(500.0, l1.getProfit(), 0.0001);

        SaleLine l2 = result.getLine(1);
        assertEquals(LocalDate.parse("2023-02-01"), l2.getLotDate());
        assertEquals(5.0, l2.getSoldQuantity(), 0.0001);
        assertEquals(120.0, l2.getBuyUnitPrice(), 0.0001);
        assertEquals(150.0, l2.getSellUnitPrice(), 0.0001);
        assertEquals(150.0, l2.getProfit(), 0.0001);

        assertEquals(5.0, position.getTotalQuantity(), 0.0001);
        PurchaseLot remainingLot = position.getLots().peekFirst();
        assertNotNull(remainingLot);
        assertEquals(LocalDate.parse("2023-02-01"), remainingLot.getPurchaseDate());
        assertEquals(5.0, remainingLot.getQuantity(), 0.0001);
    }

    @Test
    void sellingPartialFromSingleLotShouldReduceLotQuantity() {
        Share share = new Share("XYZ", "XYZ Corp", 1.0);
        Position position = new Position(share);

        position.addLot(new PurchaseLot(LocalDate.parse("2023-01-01"), 100.0, 10.0));

        SaleResult result = position.sellFifo(3.0, 150.0);

        assertEquals(1, result.getLinesCount());
        assertEquals(3.0, result.getTotalSoldQuantity(), 0.0001);
        assertEquals(150.0, result.getTotalProfit(), 0.0001); // 3*(150-100)
        assertEquals(7.0, position.getTotalQuantity(), 0.0001);
    }

    @Test
    void sellingMoreThanAvailableShouldThrowBusinessException() {
        Share share = new Share("XYZ", "XYZ Corp", 1.0);
        Position position = new Position(share);
        position.addLot(new PurchaseLot(LocalDate.parse("2023-01-01"), 100.0, 2.0));

        assertThrows(InsufficientAssetQuantityException.class, () -> position.sellFifo(3.0, 150.0));
    }
}

