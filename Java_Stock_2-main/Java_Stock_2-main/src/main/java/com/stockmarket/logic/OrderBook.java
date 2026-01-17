package com.stockmarket.logic;

import java.util.Comparator;
import java.util.PriorityQueue;

public class OrderBook {

    private final PriorityQueue<Order> queue;

    public OrderBook(OrderSide side) {
        if (side == null) throw new IllegalArgumentException("Side cannot be null");

        Comparator<Order> comparator = createComparator(side);
        this.queue = new PriorityQueue<>(comparator);
    }

    private Comparator<Order> createComparator(OrderSide side) {
        if (side == OrderSide.BUY) {
            return new Comparator<Order>() {
                @Override
                public int compare(Order o1, Order o2) {
                    // wyższy limit -> wyżej w kolejce (HEAD)
                    int byPrice = Double.compare(o2.getLimitPrice(), o1.getLimitPrice());
                    if (byPrice != 0) return byPrice;

                    // tie-breaker: wcześniejsze zlecenie pierwsze
                    return Long.compare(o1.getCreatedAtMillis(), o2.getCreatedAtMillis());
                }
            };
        } else {
            return new Comparator<Order>() {
                @Override
                public int compare(Order o1, Order o2) {
                    // dla SELL: niższa cena bardziej atrakcyjna (bliżej rynku od dołu)
                    int byPrice = Double.compare(o1.getLimitPrice(), o2.getLimitPrice());
                    if (byPrice != 0) return byPrice;

                    return Long.compare(o1.getCreatedAtMillis(), o2.getCreatedAtMillis());
                }
            };
        }
    }

    public void add(Order order) {
        queue.add(order);
    }

    public Order peek() {
        return queue.peek();
    }

    public Order poll() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }
}