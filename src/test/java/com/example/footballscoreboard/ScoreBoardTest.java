package com.example.footballscoreboard;

import com.example.footballscoreboard.domain.Game;
import com.example.footballscoreboard.domain.Score;
import com.example.footballscoreboard.domain.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ScoreBoardTest {
    private ScoreBoard scoreBoard;

    @BeforeEach
    void setUp() {
        scoreBoard = new InMemoryScoreBoard();
    }

    @Test
    void startGameShouldAddGameWithInitialScore() {
        scoreBoard.startGame("Mexico", "Canada");
        List<Game> summary = scoreBoard.getSummary();
        assertEquals(1, summary.size());
        Game game = summary.get(0);
        assertEquals("Mexico", game.homeTeam().name());
        assertEquals("Canada", game.awayTeam().name());
        assertEquals(0, game.score().home());
        assertEquals(0, game.score().away());
    }

    @Test
    void startGameShouldNotAllowDuplicateGames() {
        scoreBoard.startGame("Spain", "Brazil");
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("Spain", "Brazil"));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("spain", "brazil")); // Case-insensitivity
    }

    @Test
    void finishGameShouldRemoveGame() {
        scoreBoard.startGame("Germany", "France");
        scoreBoard.finishGame("Germany", "France");
        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void finishGameShouldBeCaseInsensitive() {
        scoreBoard.startGame("Germany", "France");
        scoreBoard.finishGame("GERMANY", "frANce");
        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void finishGameShouldThrowIfGameNotFound() {
        assertThrows(NoSuchElementException.class, () -> scoreBoard.finishGame("Uruguay", "Italy"));
    }

    @Test
    void updateScoreShouldChangeGameScore() {
        scoreBoard.startGame("Argentina", "Australia");
        scoreBoard.updateScore("Argentina", "Australia", 3, 1);
        Game game = scoreBoard.getSummary().get(0);
        assertEquals(3, game.score().home());
        assertEquals(1, game.score().away());
    }

    @Test
    void updateScoreShouldBeCaseInsensitive() {
        scoreBoard.startGame("Argentina", "Australia");
        scoreBoard.updateScore("argenTINA", "AUSTRALIA", 3, 1);
        Game game = scoreBoard.getSummary().get(0);
        assertEquals(3, game.score().home());
        assertEquals(1, game.score().away());
    }

    @Test
    void updateScoreShouldThrowIfGameNotFound() {
        assertThrows(NoSuchElementException.class, () -> scoreBoard.updateScore("Foo", "Bar", 1, 1));
    }

    @Test
    void updateScoreShouldNotAllowNegativeScores() {
        scoreBoard.startGame("A", "B");
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.updateScore("A", "B", -1, 0));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.updateScore("A", "B", 0, -1));
    }

    @Test
    void getSummaryShouldOrderByTotalScoreAndRecency() {
        scoreBoard.startGame("Mexico", "Canada"); // Total 0, T1
        scoreBoard.startGame("Spain", "Brazil");   // Total 0, T2
        scoreBoard.startGame("Germany", "France"); // Total 0, T3
        scoreBoard.startGame("Uruguay", "Italy");  // Total 0, T4
        scoreBoard.startGame("Argentina", "Australia"); // Total 0, T5

        scoreBoard.updateScore("Mexico", "Canada", 0, 5);      // Total 5 (MC, T1)
        scoreBoard.updateScore("Spain", "Brazil", 10, 2);     // Total 12 (SB, T2)
        scoreBoard.updateScore("Germany", "France", 2, 2);    // Total 4 (GF, T3)
        scoreBoard.updateScore("Uruguay", "Italy", 6, 6);      // Total 12 (UI, T4)
        scoreBoard.updateScore("Argentina", "Australia", 3, 1); // Total 4 (AA, T5)

        List<Game> summary = scoreBoard.getSummary();
        assertEquals(5, summary.size());

        // Expected order: UI (12,T4), SB (12,T2), MC (5,T1), AA (4,T5), GF (4,T3)
        assertEquals("Uruguay", summary.get(0).homeTeam().name()); // UI, Total 12, createdAt T4
        assertEquals("Spain", summary.get(1).homeTeam().name());   // SB, Total 12, createdAt T2
        assertEquals("Mexico", summary.get(2).homeTeam().name());  // MC, Total 5, createdAt T1
        assertEquals("Argentina", summary.get(3).homeTeam().name()); // AA, Total 4, createdAt T5
        assertEquals("Germany", summary.get(4).homeTeam().name()); // GF, Total 4, createdAt T3
    }

    @Test
    void teamRecordValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Team(null));
        assertThrows(IllegalArgumentException.class, () -> new Team(""));
        assertThrows(IllegalArgumentException.class, () -> new Team("  "));
        assertDoesNotThrow(() -> new Team("Valid Name"));
    }

    @Test
    void scoreRecordValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Score(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Score(0, -1));
        assertDoesNotThrow(() -> new Score(0, 0));
        assertEquals(5, new Score(2,3).total());
    }

    @Test
    void gameRecordValidationAndEquality() {
        Team t1 = new Team("Team A");
        Team t2 = new Team("Team B");
        Team t1Lower = new Team("team a"); // Same as t1, case-insensitively
        Team t2DifferentCase = new Team("Team b"); // Same as t2, case-insensitively

        // Validation: Cannot create a game with the same team for home and away
        assertThrows(IllegalArgumentException.class, () -> Game.create(t1, t1Lower), 
                     "Should throw when home and away teams are effectively the same (case-insensitive)");
        assertDoesNotThrow(() -> Game.create(t1,t2), "Should allow creation with different teams");

        // Test default record equality: Game instances are different if any component differs (e.g., createdAt or Score instance)
        Game g1 = Game.create(t1, t2);
        Game g2 = Game.create(t1Lower, t2DifferentCase); // Will have different createdAt & Score instances

        // g1 and g2 are NOT equal under default record equality because their createdAt timestamps 
        // (and Score object instances, even if logically 0-0) will differ.
        assertNotEquals(g1, g2, 
            "Game instances created at different times (or with different Score instances) should not be equal by default record equality.");
        
        // Test that two games created with identical parameters ARE equal (default record behavior)
        // This requires manually creating the same Instant and Score objects for a true test of all-field equality.
        // However, Instant.now() will always be different, so this specific scenario is hard to test without mocking Instant.
        // The assertNotEquals above sufficiently demonstrates the default record equality based on different creation moments.
    }

    @Test
    void startGameShouldNotAllowTeamToPlayMultipleGames() {
        scoreBoard.startGame("Poland", "Italy");
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("Poland", "France"), "Poland already playing");
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("Germany", "Italy"), "Italy already playing");
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("italy", "Brazil"), "Italy already playing - case insensitive");
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("France", "poland"), "Poland already playing - case insensitive");
    }
} 