package com.vitdo82.sr.scoreboard.worldcup;

import com.vitdo82.sr.scoreboard.ScoreBoard;
import com.vitdo82.sr.scoreboard.ScoreBoardException;
import com.vitdo82.sr.scoreboard.models.Match;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class FootballWorldCupScoreBoard implements ScoreBoard {

    private final Collection<Match> matches;
    private final Validator validator;

    public FootballWorldCupScoreBoard() {
        this.matches = new ConcurrentSkipListSet<>(new MatchComparator());
        this.validator = new Validator();
    }

    /**
     * Starts a new match with the given home and away team names
     * Initializes both teams' scores to 0, and sets the current time as the match start time
     *
     * @param homeTeam the name of the home team
     * @param awayTeam the name of the away team
     * @throws ScoreBoardException if a match with the same home&away teams already exists on the score board
     *                             or if the team names are invalid
     */
    @Override
    public void startMatch(String homeTeam, String awayTeam) throws ScoreBoardException {
        validator.validateNonEmpty(homeTeam, "Home team");
        validator.validateNonEmpty(awayTeam, "Away team");

        final Match match = new Match(homeTeam, awayTeam);
        if (!matches.add(match)) {
            throw new ScoreBoardException("One or both teams are already participating in another match");
        }
    }

    /**
     * Retrieves a summary of all tracked matches
     *
     * @return an unmodifiable {@link List} of {@link Match}
     */
    @Override
    public List<Match> getSummaryMatches() {
        return List.copyOf(matches);
    }

    /**
     * Updates the scores of an existing match by teams
     *
     * @param homeTeam  the name of the home team
     * @param awayTeam  the name of the away team
     * @param homeScore the updated score of the home team
     * @param awayScore the updated score of the away team
     * @throws ScoreBoardException if no match is found for the given teams or if the scores are invalid
     */
    @Override
    public void updateMatchScore(String homeTeam, String awayTeam, int homeScore, int awayScore) throws ScoreBoardException {
        validator.validateNonNegative(homeScore, "Home score");
        validator.validateNonNegative(awayScore, "Away score");

        synchronized (matches) {
            Match match = findMatch(homeTeam, awayTeam)
                    .orElseThrow(() -> new ScoreBoardException("No match found for %s and %s".formatted(homeTeam, awayTeam)));
            matches.remove(match);
            matches.add(match.updateScore(homeScore, awayScore));
        }
    }

    /**
     * Finishes a match by removing it from the score board
     * If no match is found, the method silently does nothing
     *
     * @param homeTeam the name of the home team
     * @param awayTeam the name of the away team
     * @throws ScoreBoardException if any of the team names are invalid
     */
    @Override
    public void finishMatch(String homeTeam, String awayTeam) throws ScoreBoardException {
        validator.validateNonEmpty(homeTeam, "Home team");
        validator.validateNonEmpty(awayTeam, "Away team");

        Optional<Match> matchOptional = findMatch(homeTeam, awayTeam);
        matchOptional.ifPresent(matches::remove);
    }

    /**
     * Finds a match by the home and away team names
     *
     * @param homeTeam the name of the home team
     * @param awayTeam the name of the away team
     * @return the {@link Match}
     */
    private Optional<Match> findMatch(String homeTeam, String awayTeam) {
        return matches.stream()
                .filter(m -> m.homeTeam().equals(homeTeam) && m.awayTeam().equals(awayTeam))
                .findFirst();
    }
}
