package com.stockmarket.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookPriorityTest {

    @Test
    void buyOrdersShouldPrioritizeHigherLimitPriceRegardlessOfArrivalTime() {
        OrderBook book = new OrderBook(OrderSide.BUY);

        Order a = new Order("XYZ", OrderSide.BUY, 100.0, 1.0, 1L);
        Order b = new Order("XYZ", OrderSide.BUY, 105.0, 1.0, 9999L);

        book.add(a);
        book.add(b);

        assertEquals(2, book.size());
        assertSame(b, book.peek());
        assertSame(b, book.poll());
        assertSame(a, book.poll());
        assertEquals(0, book.size());
    }

    @Test
    void buyOrdersWithSamePriceShouldPreferEarlierCreatedAt() {
        OrderBook book = new OrderBook(OrderSide.BUY);

        Order newer = new Order("XYZ", OrderSide.BUY, 100.0, 1.0, 10L);
        Order older = new Order("XYZ", OrderSide.BUY, 100.0, 1.0, 5L);

        book.add(newer);
        book.add(older);

        assertSame(older, book.peek());
    }

    @Test
    void sellOrdersShouldPrioritizeLowerLimitPrice() {
        OrderBook book = new OrderBook(OrderSide.SELL);

        Order expensive = new Order("XYZ", OrderSide.SELL, 120.0, 1.0, 1L);
        Order cheap = new Order("XYZ", OrderSide.SELL, 110.0, 1.0, 9999L);

        book.add(expensive);
        book.add(cheap);

        assertSame(cheap, book.peek());
    }
}

