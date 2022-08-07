package de.cgabrisch.rock_paper_scissors.server;

public record Round(String roundId, Player player1, Player player2, Player winner, Calls calls, int stake) {

}
