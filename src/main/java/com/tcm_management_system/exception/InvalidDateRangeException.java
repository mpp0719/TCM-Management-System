package com.tcm_management_system.exception;

import java.time.LocalDate;

public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException(LocalDate start, LocalDate end) {
        super("Start date (" + start + ") must not be after end date (" + end + ")");
    }
}