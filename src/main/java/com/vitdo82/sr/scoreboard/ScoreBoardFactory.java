package com.vitdo82.sr.scoreboard;

import com.vitdo82.sr.scoreboard.worldcup.FootballWorldCupScoreBoard;

/**
 * A factory class to create instances of different types of scoreboards
 */
public class ScoreBoardFactory {

    /**
     * Creates a new instance of the {@link FootballWorldCupScoreBoard} class
     *
     * @return a new {@link FootballWorldCupScoreBoard} instance
     */
    public static ScoreBoard createFootbalWorldCupScoreBoard() {
        return new FootballWorldCupScoreBoard();
    }
}
