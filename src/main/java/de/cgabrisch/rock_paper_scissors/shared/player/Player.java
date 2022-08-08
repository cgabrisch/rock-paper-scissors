package de.cgabrisch.rock_paper_scissors.shared.player;

public record Player(String id, String name, String clientUrl, int credit, int won, int lost) {
    public Player winning(int stake) {
        return new Player(id, name, clientUrl, credit + stake, won + 1, lost);
    }

    public Player losing(int stake) {
        return new Player(id, name, clientUrl, credit - stake, won, lost + 1);
    }
}
