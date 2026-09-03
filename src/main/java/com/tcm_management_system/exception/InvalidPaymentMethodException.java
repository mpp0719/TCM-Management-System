package com.tcm_management_system.exception;

public class InvalidPaymentMethodException extends RuntimeException {
    public InvalidPaymentMethodException(String value) {
        super("Invalid payment method: " + value + ". Must be one of: Cash, Card, Bank Transfer, E-Wallet");
    }
}