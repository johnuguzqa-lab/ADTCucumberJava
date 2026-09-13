package com.company.automation.config;

/**
 * Signals a framework-level configuration problem (missing file, unreadable property,
 * invalid value) that prevents tests from being configured correctly.
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
