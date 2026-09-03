package com.tcm_management_system.exception;
import java.math.BigDecimal;

public class InvalidPriceException extends RuntimeException {
    public InvalidPriceException(String fieldName, java.math.BigDecimal value) {
        super(fieldName + " must be greater than 0, got: " + value);
    }
}