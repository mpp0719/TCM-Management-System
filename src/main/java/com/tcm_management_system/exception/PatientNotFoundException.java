package com.tcm_management_system.exception;
public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Long id) {
        super("Patient not found: " + id);
    }
}