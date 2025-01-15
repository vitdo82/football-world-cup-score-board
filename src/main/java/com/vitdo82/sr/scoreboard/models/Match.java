package com.vitdo82.sr.scoreboard.models;

import java.time.LocalDateTime;

public record Match(
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore,
        LocalDateTime startTime
) {}
