package com.tcm_management_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({PatientNotFoundException.class, MedicineNotFoundException.class,
            VisitNotFoundException.class, PaymentNotFoundException.class})
    public ResponseEntity<String> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({InsufficientStockException.class, DuplicateIcException.class,
            InvalidIcException.class, MissingFieldException.class,
            DuplicateMedicineNameException.class, InvalidQuantityException.class,
            InvalidPriceException.class, PaymentAlreadyExistsException.class,
            InvalidPaymentMethodException.class, NegativeAmountException.class,
            VisitAlreadyPaidException.class, InvalidDateRangeException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}