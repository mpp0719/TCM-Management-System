package com.tcm_management_system.exception;

public class DuplicateIcException extends RuntimeException {
    public DuplicateIcException(String icNo) {
        super("Patient with this IC already exists: " + icNo);
    }
}