package com.example.footballscoreboard;

import com.example.footballscoreboard.domain.Game;
import com.example.footballscoreboard.domain.GameKey;
import com.example.footballscoreboard.domain.Score;
import com.example.footballscoreboard.domain.Team;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryScoreBoard implements ScoreBoard {
    // Using LinkedHashMap to preserve insertion order, relevant for tie-breaking
    // if createdAt timestamps were identical (though Instant.now() makes this rare).
    private final Map<GameKey, Game> games = new LinkedHashMap<>();

    @Override
    public void startGame(String homeTeamName, String awayTeamName) {
        Team homeTeam = new Team(homeTeamName);
        Team awayTeam = new Team(awayTeamName);

        boolean teamAlreadyPlaying = games.values().stream().anyMatch(existingGame ->
            existingGame.homeTeam().equals(homeTeam) || existingGame.awayTeam().equals(homeTeam) ||
            existingGame.homeTeam().equals(awayTeam) || existingGame.awayTeam().equals(awayTeam)
        );

        if (teamAlreadyPlaying) {
            throw new IllegalArgumentException("One or both teams are already playing a game: " +
                                               homeTeamName + " or " + awayTeamName);
        }

        GameKey gameKey = new GameKey(homeTeam, awayTeam);
        Game newGame = Game.create(homeTeam, awayTeam);
        games.put(gameKey, newGame);
    }

    @Override
    public void finishGame(String homeTeamName, String awayTeamName) {
        GameKey gameKey = new GameKey(new Team(homeTeamName), new Team(awayTeamName));
        if (games.remove(gameKey) == null) {
            throw new NoSuchElementException("Game not found: " + homeTeamName + " vs " + awayTeamName);
        }
    }

    @Override
    public void updateScore(String homeTeamName, String awayTeamName, int homeScore, int awayScore) {
        GameKey gameKey = new GameKey(new Team(homeTeamName), new Team(awayTeamName));
        Game currentGame = games.get(gameKey);

        if (currentGame == null) {
            throw new NoSuchElementException("Game not found: " + homeTeamName + " vs " + awayTeamName);
        }

        Score newScore = new Score(homeScore, awayScore);
        Game updatedGame = currentGame.withScore(newScore);
        games.put(gameKey, updatedGame);
    }

    @Override
    public List<Game> getSummary() {
        return games.values().stream()
            .sorted(Comparator.comparingInt(Game::getTotalScore)
                    .thenComparing(Game::createdAt)
                    .reversed())
            .collect(Collectors.toList());
    }
} 