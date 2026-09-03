package com.tcm_management_system.exception;

public class MedicineNotFoundException extends RuntimeException {
    public MedicineNotFoundException(Long id) {
        super("Medicine not found: " + id);
    }
}
