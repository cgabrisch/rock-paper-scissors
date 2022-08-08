package de.cgabrisch.rock_paper_scissors.botserver;

import java.util.function.Function;

import de.cgabrisch.rock_paper_scissors.shared.round.Move;
import de.cgabrisch.rock_paper_scissors.shared.round.Symbol;

class Bot {
    private final static int SYMBOL_COUNT = Symbol.values().length;
    private final String name;
    private final Function<Integer, Integer> stakeStrategy;

    Bot(String name, Function<Integer, Integer> stakeStrategy) {
        this.name = name;
        this.stakeStrategy = stakeStrategy;
    }
    
    String getName() {
        return name;
    }

    Move getMove(int credit) {
        int index = Double.valueOf(Math.random() * SYMBOL_COUNT).intValue();
        int stake = this.stakeStrategy.apply(credit);
        
        Move move = new Move(stake, Symbol.values()[index]);
        return move;
    }
}
