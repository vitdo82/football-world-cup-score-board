package com.vitdo82.sr.scoreboard;

/**
 * Custom score board exception used for reporting errors related to validation or processing
 */
public class ScoreBoardException extends Exception {

    public ScoreBoardException(String message) {
        super(message);
    }
}
