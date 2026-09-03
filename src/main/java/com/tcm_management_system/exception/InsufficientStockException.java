package com.tcm_management_system.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(int currentStock, int requested) {
        super("Not enough stock: have " + currentStock + ", tried to dispense " + requested);
    }
}
