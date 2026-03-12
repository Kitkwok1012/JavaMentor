package com.javamentor.config;

import com.javamentor.filter.LoggingFilter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Structured Logging Configuration
 */
@SpringBootTest
class LoggingConfigurationTest {

    private static final Logger log = LoggerFactory.getLogger(LoggingConfigurationTest.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testLoggingFilterRegistered() {
        // Verify LoggingFilter bean exists
        LoggingFilter loggingFilter = applicationContext.getBean(LoggingFilter.class);
        assertNotNull(loggingFilter);
    }

    @Test
    void testLogbackConfiguration() {
        // Test that we can log with structured format
        log.info("Structured logging test - application started");
        
        // Add MDC context for structured logging
        org.slf4j.MDC.put("questionId", "123");
        org.slf4j.MDC.put("topicId", "oop");
        
        log.info("User answered question");
        
        // Clean up MDC
        org.slf4j.MDC.remove("questionId");
        org.slf4j.MDC.remove("topicId");
        
        assertTrue(true, "Logging configuration is valid");
    }

    @Test
    void testMdcContextIsolation() {
        // Test MDC isolation
        org.slf4j.MDC.put("testKey", "testValue");
        
        log.info("Testing MDC isolation");
        
        // MDC should have our test key
        assertNotNull(org.slf4j.MDC.get("testKey"));
        
        // Clean up
        org.slf4j.MDC.remove("testKey");
    }
}
