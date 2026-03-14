package com.javamentor.common.utils;

import java.util.Arrays;

/**
 * Answer Utility Class
 * Centralized answer validation and normalization logic
 */
public final class AnswerUtils {

    private AnswerUtils() {
        // Utility class - no instantiation
    }

    /**
     * Check if user's answer is correct
     * Note: userAnswer is expected to be already normalized (uppercase, trimmed)
     * from AnswerService, but we also support case-insensitive comparison for flexibility
     */
    public static boolean isCorrect(String userAnswer, String correctAnswer, Boolean multiSelect) {
        if (userAnswer == null || correctAnswer == null) {
            return false;
        }
        
        if (Boolean.TRUE.equals(multiSelect)) {
            // Multi-select: normalize both and compare
            return normalizeMultiSelectAnswer(userAnswer).equals(normalizeMultiSelectAnswer(correctAnswer));
        } else {
            // Single-select: normalize correctAnswer to uppercase (userAnswer already uppercase in practice)
            String normalizedCorrect = correctAnswer.trim().toUpperCase();
            return userAnswer.toUpperCase().equals(normalizedCorrect);
        }
    }

    /**
     * Normalize multi-select answer for comparison
     * - Convert to uppercase
     * - Remove whitespace
     * - Sort letters for multi-select (e.g., "A,C" -> "A,C")
     */
    public static String normalizeMultiSelectAnswer(String answer) {
        if (answer == null) {
            return "";
        }
        
        String cleaned = answer.toUpperCase().replaceAll("\\s+", "");
        
        if (cleaned.contains(",")) {
            String[] parts = cleaned.split(",");
            Arrays.sort(parts);
            return String.join(",", parts);
        }
        
        return cleaned;
    }

    /**
     * Normalize answer (simple version for single select)
     */
    public static String normalize(String answer) {
        if (answer == null) {
            return "";
        }
        return answer.trim().toUpperCase();
    }
}
