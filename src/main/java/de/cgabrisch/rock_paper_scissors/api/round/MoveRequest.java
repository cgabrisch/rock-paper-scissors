package de.cgabrisch.rock_paper_scissors.api.round;

public record MoveRequest(String roundId, String opponent, int credit) {

}
