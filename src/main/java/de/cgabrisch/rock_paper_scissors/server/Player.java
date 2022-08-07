package de.cgabrisch.rock_paper_scissors.server;

public record Player(String id, String name, int credit, int won, int lost, String clientUrl) {
    public Player winning(int stake) {
        return new Player(id, name, credit + stake, won + 1, lost, clientUrl);
    }

    public Player losing(int stake) {
        return new Player(id, name, credit - stake, won, lost + 1, clientUrl);
    }
}
