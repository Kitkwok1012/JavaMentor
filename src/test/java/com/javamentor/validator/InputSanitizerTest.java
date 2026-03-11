package com.javamentor.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputSanitizerTest {

    private InputSanitizer sanitizer;
    
    @BeforeEach
    void setUp() {
        sanitizer = new InputSanitizer();
    }
    
    // ========== Sanitize Tests ==========
    
    @Test
    void testSanitize_nullInput() {
        assertNull(sanitizer.sanitize(null));
    }
    
    @Test
    void testSanitize_emptyInput() {
        assertEquals("", sanitizer.sanitize(""));
    }
    
    @Test
    void testSanitize_normalInput() {
        String input = "Hello World";
        assertEquals("Hello World", sanitizer.sanitize(input));
    }
    
    @Test
    void testSanitize_scriptTag() {
        String input = "Hello <script>alert('xss')</script> World";
        String result = sanitizer.sanitize(input);
        
        assertFalse(result.contains("<script>"));
        assertFalse(result.contains("alert"));
    }
    
    @Test
    void testSanitize_htmlTags() {
        String input = "<b>Bold</b> and <i>Italic</i>";
        String result = sanitizer.sanitize(input);
        
        assertFalse(result.contains("<b>"));
    }
    
    @Test
    void testSanitize_javascriptProtocol() {
        String input = "Click <a href=\"javascript:alert('xss')\">here</a>";
        String result = sanitizer.sanitize(input);
        
        assertFalse(result.contains("javascript:"));
    }
    
    // ========== Topic ID Validation Tests ==========
    
    @Test
    void testIsValidTopicId_valid() {
        assertTrue(sanitizer.isValidTopicId("oop"));
        assertTrue(sanitizer.isValidTopicId("oop-design"));
        assertTrue(sanitizer.isValidTopicId("spring_boot"));
    }
    
    @Test
    void testIsValidTopicId_invalidCharacters() {
        assertFalse(sanitizer.isValidTopicId("oop;drop table"));
        assertFalse(sanitizer.isValidTopicId("../etc/passwd"));
    }
    
    @Test
    void testIsValidTopicId_null() {
        assertFalse(sanitizer.isValidTopicId(null));
    }
    
    @Test
    void testIsValidTopicId_empty() {
        assertFalse(sanitizer.isValidTopicId(""));
    }
    
    // ========== Answer Validation Tests ==========
    
    @Test
    void testIsValidAnswer_valid() {
        assertTrue(sanitizer.isValidAnswer("A"));
        assertTrue(sanitizer.isValidAnswer("B"));
        assertTrue(sanitizer.isValidAnswer("a")); // lowercase
    }
    
    @Test
    void testIsValidAnswer_invalid() {
        assertFalse(sanitizer.isValidAnswer("E"));
        assertFalse(sanitizer.isValidAnswer("AA"));
        assertFalse(sanitizer.isValidAnswer(""));
        assertFalse(sanitizer.isValidAnswer(null));
    }
    
    // ========== SQL Injection Detection Tests ==========
    
    @Test
    void testContainsSqlInjection_select() {
        assertTrue(sanitizer.containsSqlInjection("'; SELECT * FROM users--"));
    }
    
    @Test
    void testContainsSqlInjection_drop() {
        assertTrue(sanitizer.containsSqlInjection("DROP TABLE users"));
    }
    
    @Test
    void testContainsSqlInjection_normalInput() {
        assertFalse(sanitizer.containsSqlInjection("Hello World"));
    }
    
    @Test
    void testContainsSqlInjection_null() {
        assertFalse(sanitizer.containsSqlInjection(null));
    }
}
