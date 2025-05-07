package com.example.footballscoreboard.domain;

import java.util.Objects;

// This record serves as a unique key for games in the ScoreBoard map.
// It relies on the case-insensitive equals/hashCode of the Team record.
public record GameKey(Team homeTeam, Team awayTeam) {
    public GameKey {
        Objects.requireNonNull(homeTeam, "Home team in GameKey cannot be null");
        Objects.requireNonNull(awayTeam, "Away team in GameKey cannot be null");
        // The Game record already validates that homeTeam is not equal to awayTeam.
        // If GameKey could be created independently for lookups before a Game is made,
        // a similar check might be considered here, though it's implicitly handled
        // by how ScoreBoard uses it (creates GameKey from valid team pairs).
    }

    // Default record equals and hashCode will use Team.equals() and Team.hashCode(),
    // which are already overridden for case-insensitivity.
} 