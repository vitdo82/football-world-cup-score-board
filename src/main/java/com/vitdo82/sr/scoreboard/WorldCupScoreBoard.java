package com.vitdo82.sr.scoreboard;

import com.vitdo82.sr.scoreboard.models.Match;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class WorldCupScoreBoard {

    private static final int INITIAL_SCORE = 0;

    private final Collection<Match> matches = new TreeSet<>(new MatchComparator());
    private final Validator validator = new Validator();

    /**
     * Starts a new match with the given home and away team names
     * Initializes both teams' scores to 0, and sets the current time as the match start time
     *
     * @param homeTeam the name of the home team
     * @param awayTeam the name of the away team
     * @throws ScoreBoardException if a match with the same home&away teams already exists on the score board
     */
    public void startMatch(String homeTeam, String awayTeam) throws ScoreBoardException {
        validator.validateNonEmpty(homeTeam, "Home team");
        validator.validateNonEmpty(awayTeam, "Away team");

        Match match = new Match(homeTeam, awayTeam, INITIAL_SCORE, INITIAL_SCORE, LocalDateTime.now());

        if (!matches.add(match)) {
            throw new ScoreBoardException("Match already exists");
        }
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
