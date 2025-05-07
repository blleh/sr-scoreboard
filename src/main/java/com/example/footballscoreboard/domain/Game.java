package com.example.footballscoreboard.domain;

import java.time.Instant;
import java.util.Objects;

// Game, Score, Team will be in this package

public record Game(Team homeTeam, Team awayTeam, Score score, Instant createdAt) {

    // Compact constructor for initial validation of canonical constructor arguments
    public Game { 
        Objects.requireNonNull(homeTeam, "Home team cannot be null");
        Objects.requireNonNull(awayTeam, "Away team cannot be null");
        Objects.requireNonNull(score, "Score cannot be null");
        Objects.requireNonNull(createdAt, "Creation timestamp cannot be null");
        // Team equality check ensures different teams
        if (homeTeam.equals(awayTeam)) { 
            throw new IllegalArgumentException("Home and away teams must be different: " + homeTeam.name());
        }
    }

    // Static factory method for starting a new game with an initial 0-0 score
    public static Game create(Team homeTeam, Team awayTeam) {
        return new Game(homeTeam, awayTeam, new Score(0, 0), Instant.now());
    }

    // Returns a new Game instance with the updated score, preserving other fields (immutable pattern)
    public Game withScore(Score newScore) {
        return new Game(this.homeTeam, this.awayTeam, newScore, this.createdAt);
    }

    public int getTotalScore() {
        return score.total();
    }

    // Convenience getters, primarily for testing or direct state access if needed by ScoreBoard internals.
    public String getHomeTeamName() { return homeTeam.name(); }
    public String getAwayTeamName() { return awayTeam.name(); }
    public int getHomeScore() { return score.home(); }
    public int getAwayScore() { return score.away(); }
} 