package com.vitdo82.sr.scoreboard.worldcup;

import com.vitdo82.sr.scoreboard.models.Match;

import java.util.Comparator;

/**
 * Comparator ensures that {@link Match} with later start times appear before those
 * with earlier start times when sorted
 */
public class MatchComparator implements Comparator<Match> {

    /**
     * Compares two {@link Match} objects based on their start times in descending order
     * If two matches have the same home team and away team, they are identical
     *
     * @param m1 the first match to compare
     * @param m2 the second match to compare
     */
    @Override
    public int compare(Match m1, Match m2) {
        // if both matches have the same homeTeam or awayTeam they are identical
        if (m1.awayTeam().equals(m2.awayTeam()) || m1.homeTeam().equals(m2.homeTeam())
                || m1.homeTeam().equals(m2.awayTeam()) || m1.awayTeam().equals(m2.homeTeam())) {
            return 0;
        }

        // compare total scores (homeScore + awayScore) in descending order
        final int totalScoreCompare = Integer.compare(m2.awayScore() + m2.homeScore(), m1.awayScore() + m1.homeScore());
        if (totalScoreCompare != 0) {
            return totalScoreCompare;
        }

        // if total scores the same, compare by start time in descending order
        final int timeCompare = m2.startTime().compareTo(m1.startTime());
        if (timeCompare != 0) {
            return timeCompare;
        }

        int homeTeamComparison = m1.homeTeam().compareTo(m2.homeTeam());
        if (homeTeamComparison != 0) {
            return homeTeamComparison;
        }

        return m1.awayTeam().compareTo(m2.awayTeam());
    }
}
