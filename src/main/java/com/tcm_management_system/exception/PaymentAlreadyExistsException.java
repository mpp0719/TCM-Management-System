package com.tcm_management_system.exception;

public class PaymentAlreadyExistsException extends RuntimeException {
    public PaymentAlreadyExistsException(Long visitId) {
        super("Payment already recorded for visit: " + visitId);
    }
}