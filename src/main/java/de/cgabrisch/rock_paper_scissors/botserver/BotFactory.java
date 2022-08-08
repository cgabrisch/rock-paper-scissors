package de.cgabrisch.rock_paper_scissors.botserver;

import java.util.function.Function;

import org.springframework.stereotype.Service;

@Service
class BotFactory {
    static final Function<Integer, Integer> RANDOM_STAKE = credit -> Double.valueOf(Math.random() * credit).intValue() + 1;
    static final Function<Integer, Integer> MIN_STAKE = credit -> 1;
    static final Function<Integer, Integer> MAX_STAKE = credit -> credit;
    
    Bot createBot(String name, Function<Integer, Integer> stakeStrategy) {
        return new Bot(name, stakeStrategy);
    }
}
