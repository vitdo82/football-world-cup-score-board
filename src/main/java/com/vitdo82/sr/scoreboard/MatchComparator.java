package com.vitdo82.sr.scoreboard;

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
        if (m1.awayTeam().equals(m2.awayTeam()) && m1.homeTeam().equals(m2.homeTeam())) {
            return 0;
        }

        return m2.startTime().compareTo(m1.startTime());
    }
}
