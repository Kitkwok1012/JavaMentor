package com.javamentor.config;

/**
 * Application constants
 */
public final class AppConstants {

    private AppConstants() {}

    // Follow-up options
    public static final String[] FOLLOW_UP_OPTIONS = {
        "A. 理解",
        "B. 再諗下", 
        "C. 唔識",
        "D. 其他"
    };

    // Difficulty levels
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_HARD = 3;

    // Accuracy thresholds
    public static final double ACCURACY_WEAK_TOPIC = 0.6; // 60%
    public static final int RECENT_COUNT_THRESHOLD = 3;
    public static final int WEAK_TOPIC_INJECTION_INTERVAL = 10;

    // Cookie settings
    public static final String SESSION_COOKIE_NAME = "JAVAMENTOR_SESSION";
    public static final int SESSION_COOKIE_MAX_AGE = 60 * 60 * 24 * 365; // 1 year
}
