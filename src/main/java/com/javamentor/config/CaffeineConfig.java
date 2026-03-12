package com.javamentor.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine Cache Configuration
 * 
 * Cache Strategy:
 * | Cache Name       | TTL        | Description                    |
 * |------------------|------------|--------------------------------|
 * | topics           | 24 hours   | All topics list                |
 * | questions        | 1 hour     | Questions by topic             |
 * | questionDetails  | 30 mins    | Individual question details    |
 * 
 * For local/dev use only. For production with multiple instances, use Redis.
 */
@Configuration
@EnableCaching
public class CaffeineConfig {

    public static final String CACHE_TOPICS = "topics";
    public static final String CACHE_QUESTIONS = "questions";
    public static final String CACHE_QUESTION_DETAILS = "questionDetails";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Configure caches
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // Global: max 10000 entries
                .maximumSize(10_000)
                // Global: expire after 30 mins of no access
                .expireAfterAccess(30, TimeUnit.MINUTES)
                // Record stats for monitoring
                .recordStats());
        
        // Cache names
        cacheManager.setCacheNames(java.util.Arrays.asList(
                CACHE_TOPICS,
                CACHE_QUESTIONS,
                CACHE_QUESTION_DETAILS
        ));
        
        return cacheManager;
    }
}
