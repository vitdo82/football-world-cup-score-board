package com.vitdo82.sr.scoreboard.models;

import java.time.LocalDateTime;

public record Match(
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore,
        LocalDateTime startTime
) {
    private static final int INITIAL_SCORE = 0;

    public Match(String homeTeam, String awayTeam) {
        this(homeTeam, awayTeam, INITIAL_SCORE, INITIAL_SCORE, LocalDateTime.now());
    }

    public Match updateScore(int homeScore, int awayScore) {
        return new Match(this.homeTeam, this.awayTeam, homeScore, awayScore, this.startTime);
    }
}
