package com.javamentor.utils;

import com.javamentor.common.utils.AnswerUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnswerUtils
 */
class AnswerUtilsTest {

    @Test
    void isCorrect_singleSelect_shouldMatchIgnoreCase() {
        assertTrue(AnswerUtils.isCorrect("A", "A", false));
        assertTrue(AnswerUtils.isCorrect("a", "A", false));
        assertTrue(AnswerUtils.isCorrect("A", "a", false));
        assertTrue(AnswerUtils.isCorrect("B", "b", false));
    }

    @Test
    void isCorrect_singleSelect_shouldFailOnMismatch() {
        assertFalse(AnswerUtils.isCorrect("A", "B", false));
        assertFalse(AnswerUtils.isCorrect("C", "A", false));
    }

    @Test
    void isCorrect_singleSelect_nullInput() {
        assertFalse(AnswerUtils.isCorrect(null, "A", false));
        assertFalse(AnswerUtils.isCorrect("A", null, false));
        assertFalse(AnswerUtils.isCorrect(null, null, false));
    }

    @Test
    void isCorrect_multiSelect_normalized() {
        // Test various normalization cases
        assertTrue(AnswerUtils.isCorrect("A,C", "A,C", true));
        assertTrue(AnswerUtils.isCorrect("a,c", "A,C", true));
        assertTrue(AnswerUtils.isCorrect("C,A", "A,C", true));
        assertTrue(AnswerUtils.isCorrect("A, C", "A,C", true));
        assertTrue(AnswerUtils.isCorrect("A,C", " A,C ", true));
    }

    @Test
    void isCorrect_multiSelect_wrongOrder() {
        // Order should not matter - sorted before comparison
        assertTrue(AnswerUtils.isCorrect("C,A", "A,C", true));
        assertTrue(AnswerUtils.isCorrect("B,A,C", "A,B,C", true));
    }

    @Test
    void isCorrect_multiSelect_partialMatch() {
        // Partial answers should fail
        assertFalse(AnswerUtils.isCorrect("A", "A,C", true));
        assertFalse(AnswerUtils.isCorrect("A,B", "A,C", true));
    }

    @Test
    void normalizeMultiSelectAnswer_emptyInput() {
        assertEquals("", AnswerUtils.normalizeMultiSelectAnswer(null));
        assertEquals("", AnswerUtils.normalizeMultiSelectAnswer(""));
    }

    @Test
    void normalizeMultiSelectAnswer_sorting() {
        assertEquals("A,C", AnswerUtils.normalizeMultiSelectAnswer("C,A"));
        assertEquals("A,B,C", AnswerUtils.normalizeMultiSelectAnswer("C,A,B"));
        assertEquals("A,B,C,D", AnswerUtils.normalizeMultiSelectAnswer("D,C,B,A"));
    }

    @Test
    void normalizeMultiSelectAnswer_noComma() {
        // Single letter without comma should still work
        assertEquals("A", AnswerUtils.normalizeMultiSelectAnswer("a"));
        assertEquals("B", AnswerUtils.normalizeMultiSelectAnswer("B"));
    }

    @Test
    void normalize_singleSelect() {
        assertEquals("A", AnswerUtils.normalize("a"));
        assertEquals("ABC", AnswerUtils.normalize(" ABC "));
        assertEquals("", AnswerUtils.normalize(null));
    }
}
