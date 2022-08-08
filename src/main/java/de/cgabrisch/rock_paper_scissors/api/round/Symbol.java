package de.cgabrisch.rock_paper_scissors.api.round;

public enum Symbol {
    ROCK, SCISSORS, PAPER;

    public boolean beats(Symbol other) {
        return (switch (this) {
            case PAPER -> ROCK;
            case ROCK -> SCISSORS;
            case SCISSORS -> PAPER;
        }).equals(other);
    }
}
