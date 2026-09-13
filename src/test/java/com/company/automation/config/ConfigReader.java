package com.company.automation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration manager.
 *
 * <p>Reads values from {@code config/config.properties} on the test classpath and
 * <strong>overrides them with JVM system properties</strong>, so environment-specific
 * values can be supplied on the command line without touching source files:</p>
 *
 * <pre>
 *   mvn test -Dbrowser=firefox -Dheadless=true
 * </pre>
 *
 * <p>The instance is created eagerly as a {@code static final} field: configuration is
 * effectively immutable after class-loading, which is safe for parallel execution and
 * avoids mutable static state.</p>
 */
public final class ConfigReader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);
    private static final String CONFIG_FILE = "config/config.properties";

    private static final ConfigReader INSTANCE = new ConfigReader();

    private final Properties properties;

    private ConfigReader() {
        this.properties = loadProperties();
    }

    /** Returns the process-wide configuration singleton. */
    public static ConfigReader getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the effective value for the given key: a JVM system property wins over
     * the properties file; otherwise the file value is returned; finally the supplied
     * default.
     */
    public String get(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        String fileValue = properties.getProperty(key);
        if (fileValue != null && !fileValue.isBlank()) {
            return fileValue.trim();
        }
        return defaultValue;
    }

    /** Returns the effective value for the given key, or {@code null} if absent. */
    public String get(String key) {
        return get(key, null);
    }

    /** Browser to use: {@code chrome} or {@code firefox}. */
    public String getBrowser() {
        return get("browser", "chrome");
    }

    /** Whether browsers should run in headless mode. */
    public boolean isHeadless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    /** Base URL of the application under test (may be empty for domain-only tests). */
    public String getBaseUrl() {
        return get("baseUrl", "");
    }

    /** Default explicit-wait timeout in seconds for UI automation. */
    public int getExplicitWait() {
        return parseInt("explicitWait", 10);
    }

    /** Whether failure screenshots should be captured for browser scenarios. */
    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(get("screenshotOnFailure", "true"));
    }

    private int parseInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            throw new ConfigurationException(
                    "Configuration key '" + key + "' must be an integer but was '"
                            + get(key, String.valueOf(defaultValue)) + "'", e);
        }
    }

    private Properties loadProperties() {
        Properties loaded = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new ConfigurationException(
                        "Configuration file not found on classpath: " + CONFIG_FILE);
            }
            loaded.load(input);
        } catch (IOException e) {
            throw new ConfigurationException("Failed to read configuration file: " + CONFIG_FILE, e);
        }
        LOG.info("Loaded configuration from {} ({} entries)", CONFIG_FILE, loaded.size());
        return loaded;
    }
}
