package com.company.automation.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Small, reusable helpers for handling test data. Kept deliberately tiny - only
 * add utilities here that are actually shared by more than one step definition.
 */
public final class TestDataUtils {

    private TestDataUtils() {
        // Utility class - no instantiation.
    }

    /**
     * Parses a non-empty integer quantity with a meaningful error message.
     *
     * @param value raw string value (e.g. from a DataTable cell)
     * @throws IllegalArgumentException if the value is not a valid integer
     */
    public static int parseQuantity(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Quantity must not be blank");
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid quantity '" + value + "' - expected an integer", e);
        }
    }

    /** Generates a unique name, useful for creating isolated test entities. */
    public static String uniqueName(String prefix) {
        return prefix + "_" + System.currentTimeMillis()
                + "_" + ThreadLocalRandom.current().nextInt(1000, 9999);
    }
}
