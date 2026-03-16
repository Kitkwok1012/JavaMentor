package com.javamentor.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Application configuration properties
 * Use @ConfigurationProperties for type-safe configuration
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    private String title = "JavaMentor";
    private String version = "1.0.0";
    private int rateLimitPerMinute = 60;

    public void logStartup() {
        log.info("========================================");
        log.info("  {} v{} starting...", title, version);
        log.info("  Rate limit: {} requests/min", rateLimitPerMinute);
        log.info("========================================");
    }
}
