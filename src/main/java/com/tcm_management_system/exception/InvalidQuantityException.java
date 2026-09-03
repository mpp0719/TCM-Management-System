package com.tcm_management_system.exception;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(String context, int value) {
        super(context + " must be greater than 0, got: " + value);
    }
}
