package com.tcm_management_system.exception;

public class VisitAlreadyPaidException extends RuntimeException {
    public VisitAlreadyPaidException(Long visitId) {
        super("Cannot modify medication for visit " + visitId + " — payment has already been recorded");
    }
}