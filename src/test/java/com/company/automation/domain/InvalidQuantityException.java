package com.company.automation.domain;

/**
 * Thrown when a vegetable consumption operation violates the inventory business rule:
 * <em>a user cannot consume more vegetables than are currently available</em>.
 *
 * <p>The {@link #getMessage()} value carries the exact, business-readable validation
 * message so it can be asserted against the Gherkin Examples data. Additional context
 * (available / requested quantities) is exposed through {@link #getAvailable()} and
 * {@link #getRequested()} for logging and diagnostics, without polluting the message
 * itself.</p>
 */
public class InvalidQuantityException extends RuntimeException {

    private final int available;
    private final int requested;

    public InvalidQuantityException(String message, int available, int requested) {
        super(message);
        this.available = available;
        this.requested = requested;
    }

    /** Quantity that was actually in stock when the invalid operation was attempted. */
    public int getAvailable() {
        return available;
    }

    /** Quantity the user tried to consume. */
    public int getRequested() {
        return requested;
    }
}
