package com.tcm_management_system.exception;
public class NegativeAmountException extends RuntimeException {
    public NegativeAmountException(String fieldName, java.math.BigDecimal value) {
        super(fieldName + " cannot be negative, got: " + value);
    }
}