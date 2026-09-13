package com.company.automation.driver;

import com.company.automation.config.ConfigurationException;

/** Supported WebDriver browsers. */
public enum BrowserType {
    CHROME,
    FIREFOX;

    /**
     * Parses a browser name (case-insensitive). Throws a framework configuration
     * exception for unknown values instead of silently falling back to a default.
     */
    public static BrowserType from(String name) {
        if (name == null || name.isBlank()) {
            throw new ConfigurationException(
                    "Browser name must not be blank. Use -Dbrowser=chrome or -Dbrowser=firefox.");
        }
        try {
            return BrowserType.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException(
                    "Unsupported browser '" + name
                            + "'. Supported values: chrome, firefox.", e);
        }
    }
}
