# Live Football World Cup Score Board

This project provides a lightweight library to display ongoing World Cup matches and their scores

## Features

* Start a new match in the scoreboard
* Update the match score
* Fetch match summaries
* Finish a match and remove it from the scoreboard

## Installation

### Requirements
* JDK 21

### Building

To build the project and install it in your local `M2` repository, run the following commands:

```shell
 ./mvnw install
```

### Usage

#### Add dependency

* For **Maven** projects, add the following dependency in `pom.xml`:
    ```xml
    <dependency>
        <groupId>com.vitdo82.sr</groupId>
        <artifactId>football-world-cup-score-board</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    ```
* For **Gradle** projects, add the following dependency in `build.gradle`:
    ```groovy
    implementation 'com.vitdo82.sr:football-world-cup-score-board:1.0-SNAPSHOT'
    ```

#### Example

```java
ScoreBoard worldCupScoreBoard = ScoreBoardFactory.createFootbalWorldCupScoreBoard();
worldCupScoreBoard.startMatch("Mexico", "Canada");
worldCupScoreBoard.updateMatchScore("Mexico", "Canada", 1, 2);
worldCupScoreBoard.getSummaryMatches();
worldCupScoreBoard.finishMatch("Mexico", "Canada");
```

## License

This project is licensed under the [MIT](https://choosealicense.com/licenses/mit/)
