package de.cgabrisch.rock_paper_scissors.playerregistry;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.cgabrisch.rock_paper_scissors.api.player.Opponents;
import de.cgabrisch.rock_paper_scissors.api.player.Player;
import reactor.core.publisher.Mono;

@Service
class AvailablePlayersService {
    private final static Logger log = LoggerFactory.getLogger(AvailablePlayersService.class);
    
    private final Executor checkInExecutor = Executors.newSingleThreadScheduledExecutor(
            (runnable) -> new Thread(runnable, "available-players-check-in"));
    private final Executor checkOutExecutor = Executors.newSingleThreadScheduledExecutor(
            (runnable) -> new Thread(runnable, "available-players-check-out"));
    private final BlockingQueue<Player> availablePlayers = new LinkedBlockingQueue<>();
    
    Mono<Opponents> requestOpponents() {
        return Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("Got opponents request...");
                Player player1 = availablePlayers.take();
                log.debug("First opponent: {}", player1);
                Player player2 = availablePlayers.take();
                log.debug("Second opponent: {}", player2);
                return new Opponents(player1, player2);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }, checkOutExecutor));
    }
    
    void releaseOpponents(Opponents opponents) {
        log.debug("Releasing opponents {}", opponents);
        CompletableFuture.runAsync(() -> {
            Arrays.asList(opponents.player1(), opponents.player2()).forEach(player -> {
                if (player.credit() > 0) {
                    log.debug("Adding {} to available players", player);
                    this.availablePlayers.add(player);
                } else {
                    log.debug("Will not add {} to available players - zero credits", player);
                }
            });
        }, checkInExecutor);
        
    }
    
    void addPlayer(Player player) {
        CompletableFuture.runAsync(() -> {
            if (player.credit() > 0) {
                log.debug("Adding {} to available players", player);
                this.availablePlayers.add(player);
            } else {
                log.debug("Will not add {} to available players - zero credits", player);
            }
        }, checkInExecutor);
    }
}
