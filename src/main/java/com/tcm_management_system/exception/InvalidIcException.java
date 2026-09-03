package com.tcm_management_system.exception;

public class InvalidIcException extends RuntimeException {
    public InvalidIcException(String icNo) {
        super("IC number must be 12 digits, got: " + (icNo == null ? 0 : icNo.length()));
    }
}