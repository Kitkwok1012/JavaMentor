package com.javamentor.config;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Structured Logging Tests
 */
@SpringBootTest
@ActiveProfiles("test")
class LoggingConfigTest {

    @Test
    void testMdcPutAndRemove() {
        // Test MDC put
        MDC.put("sessionId", "test-session-123");
        MDC.put("questionId", "1");
        MDC.put("topicId", "oop");
        
        assertEquals("test-session-123", MDC.get("sessionId"));
        assertEquals("1", MDC.get("questionId"));
        assertEquals("oop", MDC.get("topicId"));
        
        // Test MDC remove
        MDC.clear();
        
        assertNull(MDC.get("sessionId"));
        assertNull(MDC.get("questionId"));
        assertNull(MDC.get("topicId"));
    }

    @Test
    void testMdcWithNullValues() {
        // MDC should handle null gracefully
        MDC.put("sessionId", null);
        
        assertNotNull(MDC.getCopyOfContextMap());
        
        MDC.clear();
    }
}
