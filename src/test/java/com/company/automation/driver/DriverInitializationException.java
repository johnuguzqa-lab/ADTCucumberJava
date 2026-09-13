package com.company.automation.driver;

/** Signals that a WebDriver could not be created or configured. */
public class DriverInitializationException extends RuntimeException {

    public DriverInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DriverInitializationException(String message) {
        super(message);
    }
}
