package com.tcm_management_system.exception;

public class VisitNotFoundException extends RuntimeException {
    public VisitNotFoundException(Long id) {
        super("Visit not found: " + id);
    }
}