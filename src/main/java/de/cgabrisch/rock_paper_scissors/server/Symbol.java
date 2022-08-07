package de.cgabrisch.rock_paper_scissors.server;

public enum Symbol {
    ROCK, SCISSORS, PAPER;

    boolean beats(Symbol other) {
        return (switch (this) {
            case PAPER -> ROCK;
            case ROCK -> SCISSORS;
            case SCISSORS -> PAPER;
        }).equals(other);
    }
}
