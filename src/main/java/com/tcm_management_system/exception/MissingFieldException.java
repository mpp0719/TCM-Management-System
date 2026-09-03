package com.tcm_management_system.exception;

public class MissingFieldException extends RuntimeException {
    public MissingFieldException(String fieldName) {
        super(fieldName + " is required");
    }
}
