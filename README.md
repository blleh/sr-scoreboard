# Football World Cup Score Board

A simple Java library for managing a live Football World Cup Score Board. The solution is designed for clarity, simplicity, and testability, following TDD and clean code principles.

## Features
- Start a game (with initial score 0-0)
- Finish a game (remove from scoreboard)
- Update score (home/away)
- Get a summary of games ordered by total score and recency

## Requirements
- Java 21
- Gradle

## How to Build & Test
```
gradle build
```

## Assumptions & Design Notes
- In-memory storage (no persistence)
- No REST API or UI, just a simple library
- Domain-driven design: clear separation of domain objects and service logic
- Edge cases (e.g., duplicate games, invalid updates) are handled

---

Feel free to reach out for any questions or clarifications. 