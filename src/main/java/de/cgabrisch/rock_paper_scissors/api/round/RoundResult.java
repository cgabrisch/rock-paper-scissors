package de.cgabrisch.rock_paper_scissors.api.round;

public record RoundResult(String roundId, Result result, int stake) {
    public enum Result {
        WON, LOST, DRAW
    }
}
