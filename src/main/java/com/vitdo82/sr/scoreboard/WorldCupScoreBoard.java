package com.vitdo82.sr.scoreboard;

import com.vitdo82.sr.scoreboard.models.Match;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class WorldCupScoreBoard {

    private final Collection<Match> matches = new TreeSet<>(new MatchComparator());

    /**
     * Starts a new match with the given home and away team names
     * Initializes both teams' scores to 0, and sets the current time as the match start time
     *
     * @param homeTeam the name of the home team
     * @param awayTeam the name of the away team
     */
    public void startMatch(String homeTeam, String awayTeam) {
        Match match = new Match(homeTeam, awayTeam, 0, 0, LocalDateTime.now());
        matches.add(match);
    }

    /**
     * Retrieves a summary of all tracked matches
     *
     * @return an unmodifiable {@link List} of {@link Match}
     */
    public List<Match> getSummary() {
        return List.copyOf(matches);
    }
}
