package com.vitdo82.sr.scoreboard.worldcup;

import com.vitdo82.sr.scoreboard.ScoreBoard;
import com.vitdo82.sr.scoreboard.ScoreBoardException;
import com.vitdo82.sr.scoreboard.ScoreBoardFactory;
import com.vitdo82.sr.scoreboard.models.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Football World Cup Score Board")
class FootballWorldCupScoreBoardTest {

    private ScoreBoard worldCupScoreBoard;

    @BeforeEach
    void setupBefore() {
        this.worldCupScoreBoard = ScoreBoardFactory.createFootbalWorldCupScoreBoard();
    }

    @Nested
    @DisplayName("Scenario: Starting new matches")
    class WhenNew {

        @Test
        @DisplayName("Given two teams, when a new match is started, then it should be added to the board")
        void givenTeams_whenStarted_thenMatchAddedToTheBoard() throws ScoreBoardException {
            // Given
            String homeTeam = "Mexico";
            String awayTeam = "Canada";
            // When
            worldCupScoreBoard.startMatch(homeTeam, awayTeam);
            // Then
            assertThat(worldCupScoreBoard.getSummaryMatches())
                    .hasSize(1)
                    .first()
                    .satisfies(match -> assertMatchDetails(match, homeTeam, awayTeam, 0, 0));
        }

        @Test
        @DisplayName("Given multiple teams, when 2 matches are started, then all matches should be added to the board in order by start time")
        void givenTeams_whenStarted_thenMatchAddedToTheBoardInOrder() throws ScoreBoardException {
            // Given
            worldCupScoreBoard.startMatch("Spain", "Brazil");
            // When
            worldCupScoreBoard.startMatch("Uruguay", "Italy");
            // Then
            List<Match> matchSummary = worldCupScoreBoard.getSummaryMatches();
            assertThat(matchSummary).hasSize(2);

            assertThat(matchSummary).isSortedAccordingTo(Comparator.comparing(Match::startTime).reversed());

            assertMatchDetails(matchSummary.get(0), "Uruguay", "Italy", 0, 0);
            assertMatchDetails(matchSummary.get(1), "Spain", "Brazil", 0, 0);
        }

        @Test
        @DisplayName("Given a match, when starting the same match again, then an error should state the match already exists")
        void givenMatch_whenStartingDuplicateMatch_thenErrorOccurs() throws ScoreBoardException {
            // Given
            String homeTeam = "Mexico";
            String awayTeam = "Canada";
            worldCupScoreBoard.startMatch(homeTeam, awayTeam);
            // When
            ScoreBoardException exception = assertThrows(ScoreBoardException.class, () -> worldCupScoreBoard.startMatch(homeTeam, awayTeam));
            // Then
            assertThat(exception.getMessage()).isEqualTo("Match already exists");
        }

        @NullSource
        @EmptySource
        @ParameterizedTest(name = "{index} => home team=''{0}''")
        @DisplayName("Given an empty/null home team name, when starting the match, then an exception should be raised")
        void givenEmptyHomeTeamName_whenStartingMatch_thenRaiseException(String homeTeam) {
            // Given
            String awayTeam = "Brazil";
            // When
            ScoreBoardException exception = assertThrows(ScoreBoardException.class, () -> worldCupScoreBoard.startMatch(homeTeam, awayTeam));
            // Then
            assertThat(exception.getMessage()).isEqualTo("Home team name must not be null or empty");
        }

        @NullSource
        @EmptySource
        @ParameterizedTest(name = "{index} => away team=''{0}''")
        @DisplayName("Given an empty away team name, when starting the match, then an exception should be raised")
        void givenEmptyAwayName_whenStartingMatch_thenRaiseException(String awayTeam) {
            // Given
            String homeTeam = "Brazil";
            // When
            ScoreBoardException exception = assertThrows(ScoreBoardException.class, () -> worldCupScoreBoard.startMatch(homeTeam, awayTeam));
            // Then
            assertThat(exception.getMessage()).isEqualTo("Away team name must not be null or empty");
        }
    }

    @Nested
    @DisplayName("Scenario: Updating match score")
    class UpdateScore {

        @Test
        @DisplayName("Given a match, when the score is updated, then the updated score should be displayed correctly")
        void givenMatch_whenScoreUpdated_thenScoreDisplayedCorrectly() throws ScoreBoardException {
            // Given
            worldCupScoreBoard.startMatch("Mexico", "Canada");
            // When
            worldCupScoreBoard.updateMatchScore("Mexico", "Canada", 1, 2);
            // Then
            List<Match> matchSummary = worldCupScoreBoard.getSummaryMatches();
            assertThat(matchSummary)
                    .hasSize(1)
                    .first()
                    .satisfies(match -> assertMatchDetails(match, "Mexico", "Canada", 1, 2));
        }

        @Test
        @DisplayName("Given a not started match, when the score is updated, then an not found exception should be raised")
        void givenNotStartedMatch_whenScoreUpdated_thenScoreDisplayedCorrectly() throws ScoreBoardException {
            // Given
            worldCupScoreBoard.startMatch("Mexico", "Canada");
            // When
            ScoreBoardException exception = assertThrows(ScoreBoardException.class, () -> worldCupScoreBoard.updateMatchScore("Mexico", "Brazil", 2, 2));
            // Then
            assertThat(exception.getMessage()).isEqualTo("No match found for Mexico and Brazil");
        }

        @Test
        @DisplayName("Given a match, when the home team score is updated to a negative value, then an exception should be raised")
        void givenMatchAndNewNegativeHomeScore_whenScoreUpdated_thenRaiseException() throws ScoreBoardException {
            // Given
            worldCupScoreBoard.startMatch("Mexico", "Canada");
            // When
            ScoreBoardException exception = assertThrows(ScoreBoardException.class, () -> worldCupScoreBoard.updateMatchScore("Mexico", "Canada", -1, 2));
            // Then
            assertThat(exception.getMessage()).isEqualTo("Home score must not be negative");
        }

        @Test
        @DisplayName("Given a match, when the away team score is updated to a negative value, then an exception should be raised")
        void givenMatchAndNewNegativeAwayScore_whenScoreUpdated_thenRaiseException() throws ScoreBoardException {
            // Given
            worldCupScoreBoard.startMatch("Mexico", "Canada");
            // When
            ScoreBoardException exception = assertThrows(ScoreBoardException.class, () -> worldCupScoreBoard.updateMatchScore("Mexico", "Canada", 0, -1));
            // Then
            assertThat(exception.getMessage()).isEqualTo("Away score must not be negative");
        }
    }

    @Nested
    @DisplayName("Scenario: Retrieving match summary")
    class GetSummaryOfMatches {

        @Test
        @DisplayName("Given multiple matches, when the summary is retrieved, then it should return matches sorted by total score")
        void givenMultipleMatches_whenSummaryRequested_thenMatchesSortedByTotalScore() throws ScoreBoardException {
            // Given
            worldCupScoreBoard.startMatch("Mexico", "Canada");
            worldCupScoreBoard.updateMatchScore("Mexico", "Canada", 0, 5);

            worldCupScoreBoard.startMatch("Spain", "Brazil");
            worldCupScoreBoard.updateMatchScore("Spain", "Brazil", 10, 2);

            worldCupScoreBoard.startMatch("Germany", "France");
            worldCupScoreBoard.updateMatchScore("Germany", "France", 2, 2);

            worldCupScoreBoard.startMatch("Uruguay", "Italy");
            worldCupScoreBoard.updateMatchScore("Uruguay", "Italy", 6, 6);

            worldCupScoreBoard.startMatch("Argentina", "Australia");
            worldCupScoreBoard.updateMatchScore("Argentina", "Australia", 3, 1);
            // When
            List<Match> matchSummary = worldCupScoreBoard.getSummaryMatches();
            // Then
            assertThat(matchSummary).hasSize(5);
            assertMatchDetails(matchSummary.get(0), "Uruguay", "Italy", 6, 6);
            assertMatchDetails(matchSummary.get(1), "Spain", "Brazil", 10, 2);
            assertMatchDetails(matchSummary.get(2), "Mexico", "Canada", 0, 5);
            assertMatchDetails(matchSummary.get(3), "Argentina", "Australia", 3, 1);
            assertMatchDetails(matchSummary.get(4), "Germany", "France", 2, 2);
        }
    }

    @Nested
    @DisplayName("Scenario: Finishing a match")
    class FinishMatch {

        @Test
        @DisplayName("Given a match, when finishing the match, then the scoreboard should be empty")
        void givenMatchActive_whenFinishingMatch_thenScoreboardShouldBeEmpty() throws ScoreBoardException {
            // Given
            String homeTeam = "Mexico";
            String awayTeam = "Canada";
            worldCupScoreBoard.startMatch(homeTeam, awayTeam);
            // When
            worldCupScoreBoard.finishMatch(homeTeam, awayTeam);
            // Then
            assertThat(worldCupScoreBoard.getSummaryMatches()).isEmpty();
        }

        @Test
        @DisplayName("Given a match, when finishing a not existing match, then an exception should be raised")
        void givenMatchActive_whenFinishNotExistentMatch_thenRaiseException() throws ScoreBoardException {
            // Given
            worldCupScoreBoard.startMatch("Mexico", "Brazil");
            // When
            ScoreBoardException exception = assertThrows(ScoreBoardException.class, () -> worldCupScoreBoard.finishMatch("Mexico", "Canada"));
            // Then
            assertThat(exception.getMessage()).isEqualTo("No match found for Mexico and Canada");
        }

        @NullSource
        @EmptySource
        @ParameterizedTest(name = "{index} => home team=''{0}''")
        @DisplayName("Given an empty/null home team name, when finishing the match, then an exception should be raised")
        void givenEmptyHomeTeamName_whenFinishMatch_thenRaiseException(String homeTeam) {
            // Given
            String awayTeam = "Brazil";
            // When
            ScoreBoardException exception = assertThrows(ScoreBoardException.class, () -> worldCupScoreBoard.finishMatch(homeTeam, awayTeam));
            // Then
            assertThat(exception.getMessage()).isEqualTo("Home team name must not be null or empty");
        }

        @NullSource
        @EmptySource
        @ParameterizedTest(name = "{index} => away team=''{0}''")
        @DisplayName("Given an empty/null away team name, when finishing the match, then an exception should be raised")
        void givenEmptyAwayName_whenFinishMatch_thenRaiseException(String awayTeam) {
            // Given
            String homeTeam = "Brazil";
            // When
            ScoreBoardException exception = assertThrows(ScoreBoardException.class, () -> worldCupScoreBoard.finishMatch(homeTeam, awayTeam));
            // Then
            assertThat(exception.getMessage()).isEqualTo("Away team name must not be null or empty");
        }
    }

    /**
     * Asserts the match details
     *
     * @param match             object to verify
     * @param expectedHomeTeam  the expected name of the home team
     * @param expectedAwayTeam  the expected name of the away team
     * @param expectedHomeScore the expected score of the home team
     * @param expectedAwayScore the expected score of the away team
     */
    private void assertMatchDetails(Match match, String expectedHomeTeam, String expectedAwayTeam, int expectedHomeScore, int expectedAwayScore) {
        assertThat(match.homeTeam()).isEqualTo(expectedHomeTeam);
        assertThat(match.awayTeam()).isEqualTo(expectedAwayTeam);
        assertThat(match.homeScore()).isEqualTo(expectedHomeScore);
        assertThat(match.awayScore()).isEqualTo(expectedAwayScore);
    }
}
