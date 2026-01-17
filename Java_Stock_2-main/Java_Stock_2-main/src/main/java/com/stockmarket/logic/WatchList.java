package com.stockmarket.logic;

import java.util.HashSet;
import java.util.Set;

public class WatchList {

    private final Set<String> symbols = new HashSet<>();

    public boolean add(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("Symbol cannot be null");
        return symbols.add(symbol);
    }

    public boolean contains(String symbol) {
        return symbols.contains(symbol);
    }

    public int size() {
        return symbols.size();
    }

    public Set<String> getSymbols() {
        return symbols;
    }
}