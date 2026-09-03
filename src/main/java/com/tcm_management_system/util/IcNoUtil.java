package com.tcm_management_system.util;
import com.tcm_management_system.exception.InvalidIcException;

public class IcNoUtil {
    public static String normalize(String icNo) {
        return icNo == null ? null : icNo.replaceAll("[^0-9]", "");
    }

    public static String normalizeAndValidate(String icNo) {
        String digits = normalize(icNo);
        if (digits == null || digits.length() != 12) {
            throw new InvalidIcException(digits);
        }
        return digits;
    }

    public static String formatWithDashes(String digitsOnly) {
        if (digitsOnly == null || digitsOnly.length() != 12) return digitsOnly;
        return digitsOnly.substring(0, 6) + "-" + digitsOnly.substring(6, 8) + "-" + digitsOnly.substring(8);
    }
}