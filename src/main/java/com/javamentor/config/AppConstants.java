package com.javamentor.config;

/**
 * Pure static constants - no Spring needed
 */
public final class AppConstants {

    private AppConstants() {
        // Utility class - no instantiation
    }

    // ==================== Session ====================
    
    public static final String SESSION_COOKIE_NAME = "JM_SESSION";
    public static final int SESSION_COOKIE_MAX_AGE_SECONDS = 365 * 24 * 60 * 60; // 1 year in seconds
    
    // ==================== Difficulty Levels ====================
    
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_HARD = 3;

    // ==================== Cache Names ====================
    
    public static final String CACHE_TOPICS = "topics";
    public static final String CACHE_QUESTIONS = "questions";
    public static final String CACHE_PROGRESS = "progress";

    // ==================== Recommender Rules ====================
    
    public static final int RECENT_COUNT_THRESHOLD = 5;
    public static final int WEAK_TOPIC_INJECTION_INTERVAL = 3;
    public static final int MAX_RECOMMEND_ATTEMPTS = 10;
    public static final int FOLLOW_UP_OPTIONS = 3;
    public static final double ACCURACY_WEAK_TOPIC = 0.6;
    public static final double ACCURACY_STRONG_TOPIC = 0.85;
}
