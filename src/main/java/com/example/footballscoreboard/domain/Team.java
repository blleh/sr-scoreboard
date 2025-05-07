package com.example.footballscoreboard.domain;

import java.util.Objects;

public record Team(String name) {
    public Team {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Team name must not be null or blank");
        }
        // Note: Team name is used as is; trimming or further normalization is not performed by this record.
        // Comparison is case-insensitive via overridden equals/hashCode.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return name.equalsIgnoreCase(team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }
} 