package com.vitdo82.sr.scoreboard;

import com.vitdo82.sr.scoreboard.models.Match;

import java.util.List;

/**
 * Interface representing the operations for a score board
 */
public interface ScoreBoard {

    /**
     * Starts a new match with the given home and away team names
     */
    void startMatch(String homeTeam, String awayTeam) throws ScoreBoardException;

    /**
     * Retrieves a summary of all tracked matches
     */
    List<Match> getSummaryMatches();

    /**
     * Updates the scores of an existing match by teams
     */
    void updateMatchScore(String homeTeam, String awayTeam, int homeScore, int awayScore) throws ScoreBoardException;

    /**
     * Finishes a match by removing it from the scoreboard
     */
    void finishMatch(String homeTeam, String awayTeam) throws ScoreBoardException;
}
