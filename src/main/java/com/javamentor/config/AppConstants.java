package com.javamentor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Application constants - centralized configuration
 */
@Component
public class AppConstants {

    private static final Logger log = LoggerFactory.getLogger(AppConstants.class);

    @Value("${app.title:JavaMentor}")
    private String appTitle;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${ratelimit.requests-per-minute:60}")
    private int rateLimitPerMinute;

    // Session cookie name
    public static final String SESSION_COOKIE_NAME = "JM_SESSION";
    
    // Default difficulty levels
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_HARD = 3;

    // Cache names
    public static final String CACHE_TOPICS = "topics";
    public static final String CACHE_QUESTIONS = "questions";
    public static final String CACHE_PROGRESS = "progress";

    // Follow-up recommender constants
    public static final int RECENT_COUNT_THRESHOLD = 5;
    public static final int WEAK_TOPIC_INJECTION_INTERVAL = 3;
    public static final int MAX_RECOMMEND_ATTEMPTS = 10;
    public static final int FOLLOW_UP_OPTIONS = 3;
    public static final double ACCURACY_WEAK_TOPIC = 0.6;
    public static final double ACCURACY_STRONG_TOPIC = 0.85;

    public String getAppTitle() {
        return appTitle;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public int getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }

    public void logStartup() {
        log.info("========================================");
        log.info("  {} v{} starting...", appTitle, appVersion);
        log.info("  Rate limit: {} requests/min", rateLimitPerMinute);
        log.info("========================================");
    }
}
