package de.cgabrisch.rock_paper_scissors.server;

public record Player(String id, String name, int credit, int won, int lost, String clientUrl) {

}
