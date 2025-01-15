package com.vitdo82.sr.scoreboard.worldcup;

import com.vitdo82.sr.scoreboard.ScoreBoardException;

/**
 * Utility class provide validation operations on input data
 */
public class Validator {

    /**
     * Validates that the provided name is not null or not empty
     *
     * @param name      the value to validate
     * @param fieldName the name of the field being validated
     * @throws ScoreBoardException an exception will be thrown with a message indicating which field is invalid
     */
    public void validateNonEmpty(String name, String fieldName) throws ScoreBoardException {
        if (name == null || name.trim().isEmpty()) {
            throw new ScoreBoardException("%s name must not be null or empty".formatted(fieldName));
        }
    }

    /**
     * Validates that the provided score is not negative
     *
     * @param score     the value to validate
     * @param fieldName the name of the field being validated
     * @throws ScoreBoardException an exception will be thrown with a message indicating which field is invalid
     */
    public void validateNonNegative(int score, String fieldName) throws ScoreBoardException {
        if (score < 0) {
            throw new ScoreBoardException("%s must not be negative".formatted(fieldName));
        }
    }
}
