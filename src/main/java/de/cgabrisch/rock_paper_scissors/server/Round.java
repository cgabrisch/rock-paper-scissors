package de.cgabrisch.rock_paper_scissors.server;

public record Round(Player player1, Player player2, Player winner, Calls calls, int stake) {

}
