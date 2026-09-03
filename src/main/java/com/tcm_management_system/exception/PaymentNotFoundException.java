package com.tcm_management_system.exception;

public class PaymentNotFoundException extends RuntimeException {
    private PaymentNotFoundException(String message) {
        super(message);
    }
    public static PaymentNotFoundException forVisit(Long visitId) {
        return new PaymentNotFoundException("No payment recorded yet for visit: " + visitId);
    }
    public static PaymentNotFoundException forPaymentId(Long paymentId) {
        return new PaymentNotFoundException("Payment not found: " + paymentId);
    }
}