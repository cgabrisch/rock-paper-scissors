package de.cgabrisch.rock_paper_scissors.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
public class AvailablePlayersService {
    private final static Logger log = LoggerFactory.getLogger(AvailablePlayersService.class);
    
    private final Executor checkInExecutor = Executors.newSingleThreadScheduledExecutor(
            (runnable) -> new Thread(runnable, "available-players-check-in"));
    private final Executor checkOutExecutor = Executors.newSingleThreadScheduledExecutor(
            (runnable) -> new Thread(runnable, "available-players-check-out"));
    private final BlockingQueue<Player> availablePlayers = new LinkedBlockingQueue<>();
    
    AvailablePlayersService() {
        availablePlayers.add(new Player("1", "Player 1", 101, 1, 0, "http://localhost:8081"));
        availablePlayers.add(new Player("2", "Player 2", 99, 0, 1, "http://localhost:8082"));
        availablePlayers.add(new Player("3", "Player 3", 99, 0, 1, "http://localhost:8083"));
    }
    
    public Mono<Tuple2<Player, Player>> checkOutPairOfPlayers() {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("Got check out request...");
                Player player1 = availablePlayers.take();
                log.debug("Checked out first: {}", player1);
                Player player2 = availablePlayers.take();
                log.debug("Checked out second: {}", player2);
                return Tuples.of(player1, player2);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }, checkOutExecutor));
    }
    
    public void checkInPlayer(Player player) {
        CompletableFuture.runAsync(() -> {
            log.debug("Checking in {}", player);
            this.availablePlayers.add(player);
        }, checkInExecutor);
    }
}
