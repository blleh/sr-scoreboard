package com.example.footballscoreboard;

import com.example.footballscoreboard.domain.Game;
import java.util.List;

public interface ScoreBoard {
    void startGame(String homeTeamName, String awayTeamName);
    void finishGame(String homeTeamName, String awayTeamName);
    void updateScore(String homeTeamName, String awayTeamName, int homeScore, int awayScore);
    List<Game> getSummary();
} 