package com.javamentor.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Input Sanitizer - Prevents XSS and injection attacks
 */
@Component
public class InputSanitizer {

    // Dangerous patterns - simplified SQL patterns
    private static final Pattern SQL_PATTERN = Pattern.compile(
        "(?i)(\\bSELECT\\b|\\bINSERT\\b|\\bUPDATE\\b|\\bDELETE\\b|\\bDROP\\b|\\bUNION\\b|\\bALTER\\b|\\bCREATE\\b|\\bTRUNCATE\\b|--|;|'|\")",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Sanitize string input - remove dangerous characters
     */
    public String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String sanitized = input;
        
        // Remove script tags
        sanitized = sanitized.replaceAll("(?i)<script[^>]*>.*?</script>", "");
        sanitized = sanitized.replaceAll("(?i)<script[^>]*/>", "");
        
        // Remove javascript: protocol
        sanitized = sanitized.replaceAll("(?i)javascript:", "");
        
        // Remove HTML tags
        sanitized = sanitized.replaceAll("<[^>]+>", "");
        
        // Escape HTML entities
        sanitized = escapeHtml(sanitized);

        return sanitized.trim();
    }

    /**
     * Validate topic ID format (alphanumeric, dash, underscore only)
     */
    public boolean isValidTopicId(String topicId) {
        if (topicId == null || topicId.isEmpty()) {
            return false;
        }
        return topicId.matches("^[a-zA-Z0-9_-]+$") && topicId.length() <= 50;
    }

    /**
     * Validate answer format
     */
    public boolean isValidAnswer(String answer) {
        if (answer == null || answer.isEmpty()) {
            return false;
        }
        // Only allow A, B, C, D (case insensitive)
        return answer.matches("^[A-Da-d]$") && answer.length() == 1;
    }

    /**
     * Check for potential SQL injection
     */
    public boolean containsSqlInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return SQL_PATTERN.matcher(input).find();
    }

    /**
     * Escape HTML entities
     */
    private String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
    }
}
