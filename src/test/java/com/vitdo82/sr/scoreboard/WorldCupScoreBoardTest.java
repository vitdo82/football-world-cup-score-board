package com.vitdo82.sr.scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("World Cup Score Board")
class WorldCupScoreBoardTest {

    private WorldCupScoreBoard worldCupScoreBoard;

    @BeforeEach
    void setupBefore() {
        this.worldCupScoreBoard = new WorldCupScoreBoard();
    }

    @Nested
    @DisplayName("when start a new match")
    class WhenNew {

        @Test
        @DisplayName("successfully start a new match")
        void successfullyStartNewMatchTest() {
            // Given
            String homeTeam = "Mexico";
            String awayTeam = "Canada";

            // When
            worldCupScoreBoard.startMatch(homeTeam, awayTeam);

            // Then
            assertThat(worldCupScoreBoard.getSummary())
                    .hasSize(1)
                    .first()
                    .satisfies(match -> {
                        assertThat(match.homeTeam()).isEqualTo(homeTeam);
                        assertThat(match.homeScore()).isEqualTo(0);

                        assertThat(match.awayTeam()).isEqualTo(awayTeam);
                        assertThat(match.awayScore()).isEqualTo(0);
                    });
        }
    }
}
