package de.cgabrisch.rock_paper_scissors.playerregistry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import de.cgabrisch.rock_paper_scissors.shared.player.Opponents;
import de.cgabrisch.rock_paper_scissors.shared.player.Player;
import reactor.core.publisher.Mono;

@Repository
class PlayerRepository {
    private final static Logger log = LoggerFactory.getLogger(PlayerRepository.class);

    private final Executor requestOppoentsExecutor = Executors
            .newSingleThreadScheduledExecutor((runnable) -> new Thread(runnable, "opponents-request"));
    private final BlockingQueue<Player> availablePlayers = new LinkedBlockingQueue<>();

    private final ConcurrentMap<String, String> playerNamesToIds = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Player> playerIdsToPlayers = new ConcurrentHashMap<>();
    
    Mono<Opponents> requestOpponents() {
        // We take a pair of players in a single thread only to avoid race conditions
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
        }, requestOppoentsExecutor));
    }

    void releaseOpponents(Opponents opponents) {
        log.debug("Releasing opponents {}", opponents);
        Arrays.asList(opponents.player1(), opponents.player2()).forEach(player -> {
            Player previousPlayer = this.playerIdsToPlayers.replace(player.id(), player);
            if (previousPlayer == null) {
                log.error("Released unknown player {}", player);
                return;
            }
            
            if (player.credit() > 0) {
                log.debug("Adding {} to available players", player);
                this.availablePlayers.add(player);
            } else {
                log.debug("Will not add {} to available players - zero credits", player);
            }
        });
    }

    void addPlayer(Player player) {
        String previousId = this.playerNamesToIds.putIfAbsent(player.name(), player.id());
        if (previousId != null) {
            // TODO Propagate this state to the caller
            log.error("A player with name {} exists already", player.name());
            return;
        }
        
        Player previousPlayer = playerIdsToPlayers.putIfAbsent(player.id(), player);
        if (previousPlayer != null) {
            // If this ever happens, then our id generation is broken
            log.error("A player with id {} exists already", player.id());
            return;
        }
        
        if (player.credit() > 0) {
            log.debug("Adding {} to available players", player);
            this.availablePlayers.add(player);
        } else {
            log.debug("Will not add {} to available players - zero credits", player);
        }
    }
    
    Optional<Player> getPlayer(String playerId) {
        return Optional.ofNullable(this.playerIdsToPlayers.get(playerId));
    }
    
    Collection<Player> getAllPlayers() {
        return this.playerIdsToPlayers.values();
    }
}
