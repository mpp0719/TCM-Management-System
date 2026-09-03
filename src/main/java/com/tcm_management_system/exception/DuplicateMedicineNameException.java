package com.tcm_management_system.exception;

public class DuplicateMedicineNameException extends RuntimeException {
    public DuplicateMedicineNameException(String name) {
        super("Medicine with this name already exists: " + name);
    }
}