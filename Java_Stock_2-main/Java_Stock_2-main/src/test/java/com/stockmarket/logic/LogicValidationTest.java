package com.stockmarket.logic;

import com.stockmarket.domain.PurchaseLot;
import com.stockmarket.domain.Share;
import com.stockmarket.exceptions.DataIntegrityException;
import com.stockmarket.exceptions.InsufficientAssetQuantityException;
import com.stockmarket.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LogicValidationTest {

    @Test
    void portfolioShouldValidateConstructorAndGetPositionArgs() {
        assertThrows(IllegalArgumentException.class, () -> new Portfolio(-1.0));

        Portfolio p = new Portfolio(0.0);
        assertThrows(IllegalArgumentException.class, () -> p.getPosition(null));
        assertEquals(0, p.getHoldingsCount());
        assertEquals(0.0, p.calculateAssetsRealValue(), 0.0001);
        assertEquals(0.0, p.calculateTotalValue(), 0.0001);
    }

    @Test
    void portfolioBuyShouldValidateArgsAndThrowBusinessExceptionWhenNoCash() {
        Portfolio p = new Portfolio(0.0);
        Share share = new Share("AAPL", "Apple", 100.0);

        assertThrows(IllegalArgumentException.class, () -> p.buyAsset(null, 1.0, LocalDate.now(), 1.0));
        assertThrows(IllegalArgumentException.class, () -> p.buyAsset(share, 0.0, LocalDate.now(), 1.0));
        assertThrows(InsufficientFundsException.class, () -> p.buyAsset(share, 1.0, LocalDate.now(), 100.0));
    }

    @Test
    void portfolioShouldAddLotsToExistingPositionAndUpdateCash() {
        Portfolio p = new Portfolio(1000.0);
        Share share = new Share("AAPL", "Apple", 100.0);

        p.buyAsset(share, 1.0, LocalDate.parse("2023-01-01"), 100.0); // cost = market*qty + fee = 105
        p.buyAsset(share, 2.0, LocalDate.parse("2023-02-01"), 100.0); // cost = 205

        assertEquals(1, p.getHoldingsCount());
        assertNotNull(p.getPosition("AAPL"));
        assertEquals(3.0, p.getPosition("AAPL").getTotalQuantity(), 0.0001);
        assertEquals(690.0, p.getCash(), 0.0001);
    }

    @Test
    void portfolioSellShouldValidateSymbolAndRemovePositionWhenSoldOut() {
        Portfolio p = new Portfolio(0.0);
        Share share = new Share("XYZ", "XYZ", 1.0);
        Position pos = new Position(share);
        pos.addLot(new PurchaseLot(LocalDate.parse("2023-01-01"), 10.0, 2.0));
        p.getPositions().put("XYZ", pos);

        assertThrows(IllegalArgumentException.class, () -> p.sellAsset("NOPE", 1.0, 1.0, LocalDate.now()));

        SaleResult sale = p.sellAsset("XYZ", 2.0, 15.0, LocalDate.parse("2024-01-01"));
        assertEquals(2.0, sale.getTotalSoldQuantity(), 0.0001);
        assertEquals(30.0, p.getCash(), 0.0001);
        assertNull(p.getPosition("XYZ"));
        assertEquals(0, p.getHoldingsCount());
    }

    @Test
    void positionShouldValidateConstructorAndSellInputs() {
        assertThrows(IllegalArgumentException.class, () -> new Position(null));

        Position p = new Position(new Share("XYZ", "XYZ", 1.0));
        assertThrows(IllegalArgumentException.class, () -> p.addLot(null));

        assertThrows(IllegalArgumentException.class, () -> p.sellFifo(0.0, 10.0));
        assertThrows(IllegalArgumentException.class, () -> p.sellFifo(1.0, -1.0));
        assertThrows(InsufficientAssetQuantityException.class, () -> p.sellFifo(1.0, 10.0));
    }

    @Test
    void saleResultAndSaleLineShouldValidateGetLineBoundsAndComputeProfit() {
        SaleLine[] lines = new SaleLine[1];
        lines[0] = new SaleLine(LocalDate.parse("2023-01-01"), 2.0, 10.0, 15.0);

        SaleResult result = new SaleResult("XYZ", 2.0, lines, 1, lines[0].getProfit());

        assertEquals("XYZ", result.getSymbol());
        assertEquals(2.0, result.getTotalSoldQuantity(), 0.0001);
        assertEquals(1, result.getLinesCount());
        assertEquals(10.0, result.getTotalProfit(), 0.0001);

        assertThrows(IndexOutOfBoundsException.class, () -> result.getLine(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> result.getLine(1));
        assertEquals(10.0, result.getLine(0).getProfit(), 0.0001);
    }

    @Test
    void orderAndOrderBookAndWatchListShouldValidateInputs() {
        assertThrows(IllegalArgumentException.class, () -> new Order(null, OrderSide.BUY, 1.0, 1.0, 1L));
        assertThrows(IllegalArgumentException.class, () -> new Order("XYZ", null, 1.0, 1.0, 1L));
        assertThrows(IllegalArgumentException.class, () -> new Order("XYZ", OrderSide.BUY, -1.0, 1.0, 1L));
        assertThrows(IllegalArgumentException.class, () -> new Order("XYZ", OrderSide.BUY, 1.0, 0.0, 1L));

        Order o = new Order("XYZ", OrderSide.BUY, 10.0, 2.0, 123L);
        assertEquals("XYZ", o.getSymbol());
        assertEquals(OrderSide.BUY, o.getSide());
        assertEquals(10.0, o.getLimitPrice(), 0.0001);
        assertEquals(2.0, o.getQuantity(), 0.0001);
        assertEquals(123L, o.getCreatedAtMillis());

        assertThrows(IllegalArgumentException.class, () -> new OrderBook(null));

        WatchList wl = new WatchList();
        assertThrows(IllegalArgumentException.class, () -> wl.add(null));
        assertTrue(wl.getSymbols().isEmpty());

        // enum coverage
        assertEquals(2, OrderSide.values().length);
    }

    @Test
    void exceptionsShouldCarryMessageAndCause() {
        RuntimeException cause = new RuntimeException("cause");
        DataIntegrityException ex = new DataIntegrityException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}

