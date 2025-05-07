package com.example.footballscoreboard.domain;

public record Score(int home, int away) {
    public Score {
        if (home < 0 || away < 0) {
            throw new IllegalArgumentException("Scores must be non-negative");
        }
    }

    public int total() {
        return home + away;
    }
} 