package com.vitdo82.sr.scoreboard.worldcup;

import com.vitdo82.sr.scoreboard.ScoreBoard;
import com.vitdo82.sr.scoreboard.ScoreBoardException;
import com.vitdo82.sr.scoreboard.ScoreBoardFactory;
import com.vitdo82.sr.scoreboard.models.Match;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

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
            assertThat(worldCupScoreBoard.getSummaryMatches()).hasSize(1).first().satisfies(match -> assertMatchDetails(match, homeTeam, awayTeam, 0, 0));
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
        @DisplayName("Given a match, when starting a new match with one of the same teams, then an error should state the match already exists")
        void givenMatch_whenStartingDuplicateTeamMatch_thenErrorOccurs() throws ScoreBoardException {
            // Given
            worldCupScoreBoard.startMatch("Spain", "Brazil");
            // When
            ScoreBoardException exception = assertThrows(ScoreBoardException.class, () -> worldCupScoreBoard.startMatch("Uruguay", "Spain"));
            // Then
            assertThat(exception.getMessage()).isEqualTo("One or both teams are already participating in another match");
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
            assertThat(exception.getMessage()).isEqualTo("One or both teams are already participating in another match");
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

        @Test
        @DisplayName("Given team names, when multiple matches are started in parallel, then all matches should be added to the board")
        void givenTeamNames_whenMultipleMatchStarted_thenMatchAddedToTheBoard() throws InterruptedException {
            // Given
            final String homeTeam = "Mexico";
            final String awayTeam = "Brazil";
            int numberOfMatches = 1000;

            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                CountDownLatch latch = new CountDownLatch(numberOfMatches);

                // When
                IntStream.range(1, numberOfMatches + 1).forEach(index -> executor.submit(() -> {
                    try {
                        worldCupScoreBoard.startMatch(homeTeam + index, awayTeam + (index * 10000));
                    } catch (ScoreBoardException e) {
                        Assertions.fail("Start match failed: %s", e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }));
                latch.await();
                executor.shutdown();
            }

            // Then
            assertThat(worldCupScoreBoard.getSummaryMatches()).hasSize(numberOfMatches);
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
            assertThat(matchSummary).hasSize(1).first().satisfies(match -> assertMatchDetails(match, "Mexico", "Canada", 1, 2));
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

        @Test
        @DisplayName("Given a list of matches, when the scores are updated and finished, then all matches should be deleted from the board")
        void givenListMatches_whenScoreUpdatedFinished_thenMatchesDeletedFromTheBoard() throws InterruptedException {

            // Given
            final String homeTeam = "Mexico";
            final String awayTeam = "Brazil";
            final int uniqIndex = 10000;
            int numberOfStartMatches = 100;

            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                CountDownLatch latch = new CountDownLatch(numberOfStartMatches);
                IntStream.range(1, numberOfStartMatches + 1).forEach(index -> executor.submit(() -> {
                    try {
                        worldCupScoreBoard.startMatch(homeTeam + index, awayTeam + (index * uniqIndex));
                    } catch (ScoreBoardException e) {
                        Assertions.fail("Start match failed: %s", e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }));
                latch.await();
            }

            // When
            final int numThreads = 50;
            CountDownLatch latch = new CountDownLatch(numThreads * (numberOfStartMatches * 2));
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < numThreads; i++) {
                    final int homeScore = i;  // Each thread sets a different score
                    final int awayScore = i + 1;  // Away score is home score + 1
                    IntStream.range(1, numberOfStartMatches + 1).forEach(index -> {
                        final String homeTeamName = homeTeam + index;
                        final String awayTeamName = awayTeam + (index * uniqIndex);
                        executor.submit(() -> {
                            try {
                                worldCupScoreBoard.updateMatchScore(homeTeamName, awayTeamName, homeScore, awayScore);
                            } catch (ScoreBoardException e) {
                                Assertions.fail("Update match score failed: %s", e.getMessage());
                            } finally {
                                latch.countDown();
                            }
                        });
                        executor.submit(() -> {
                            try {
                                worldCupScoreBoard.finishMatch(homeTeamName, awayTeamName);
                            } catch (ScoreBoardException e) {
                                Assertions.fail("Finish match failed: %s", e.getMessage());
                            } finally {
                                latch.countDown();
                            }
                        });
                    });
                }
                latch.await();
            }

            // Then
            assertThat(worldCupScoreBoard.getSummaryMatches()).isEmpty();
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
        @DisplayName("Given a match, when finishing a not existing match, then no changes occur")
        void givenMatchActive_whenFinishNotExistentMatch_thenNoChangesOccur() throws ScoreBoardException {
            // Given
            worldCupScoreBoard.startMatch("Mexico", "Brazil");
            // When
            worldCupScoreBoard.finishMatch("Mexico", "Canada");
            // Then
            List<Match> matchSummary = worldCupScoreBoard.getSummaryMatches();
            assertThat(matchSummary).hasSize(1).first().satisfies(match -> assertMatchDetails(match, "Mexico", "Brazil", 0, 0));

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

        @Test
        @DisplayName("Given a list of matches, when matches finished, then all matches should be deleted from the board")
        void givenListMatches_whenMatchesFinished_thenMatchesDeletedFromTheBoard() throws InterruptedException {
            // Given
            final String homeTeam = "Mexico";
            final String awayTeam = "Brazil";
            int numberOfMatches = 100;
            int uniqIndex = 100000;
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                CountDownLatch latch = new CountDownLatch(numberOfMatches);
                IntStream.range(1, numberOfMatches + 1).forEach(index -> executor.submit(() -> {
                    try {
                        worldCupScoreBoard.startMatch(homeTeam + index, awayTeam + (index * uniqIndex));
                    } catch (ScoreBoardException e) {
                        Assertions.fail("Start match failed: %s", e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }));
                latch.await();
            }

            // When
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                final CountDownLatch latch = new CountDownLatch(numberOfMatches);
                IntStream.range(1, numberOfMatches + 1).forEach(index -> {
                    final String homeTeamName = homeTeam + index;
                    final String awayTeamName = awayTeam + (index * uniqIndex);
                    executor.submit(() -> {
                        try {
                            worldCupScoreBoard.finishMatch(homeTeamName, awayTeamName);
                        } catch (ScoreBoardException e) {
                            Assertions.fail("Finish match failed: %s", e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                    });
                });
                latch.await();
            }

            // Then
            assertThat(worldCupScoreBoard.getSummaryMatches()).isEmpty();
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
