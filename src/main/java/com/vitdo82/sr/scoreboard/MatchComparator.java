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
     *
     * @param m1 the first match to compare
     * @param m2 the second match to compare
     */
    @Override
    public int compare(Match m1, Match m2) {
        return m2.startTime().compareTo(m1.startTime());
    }
}
