package com.stockmarket.exceptions;

public class InsufficientAssetQuantityException extends RuntimeException {
    public InsufficientAssetQuantityException(String message) {
        super(message);
    }
}