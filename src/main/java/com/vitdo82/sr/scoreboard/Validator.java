package com.vitdo82.sr.scoreboard;

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
}
