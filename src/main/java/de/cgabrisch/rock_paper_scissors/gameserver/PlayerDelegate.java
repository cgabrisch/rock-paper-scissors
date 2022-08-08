package de.cgabrisch.rock_paper_scissors.gameserver;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import de.cgabrisch.rock_paper_scissors.shared.player.Player;
import de.cgabrisch.rock_paper_scissors.shared.round.Move;
import de.cgabrisch.rock_paper_scissors.shared.round.MoveRequest;
import de.cgabrisch.rock_paper_scissors.shared.round.RoundResult;
import de.cgabrisch.rock_paper_scissors.shared.round.RoundResult.Result;
import reactor.core.publisher.Mono;

@Service
class PlayerDelegate { 
    Mono<Move> getMoveFromPlayer(Player player, String roundId, String opponentName) {
        return WebClient.create(player.clientUrl()).post().uri("/round/call/{playerId}", player.id()).bodyValue(new MoveRequest(roundId, opponentName)).retrieve().bodyToMono(Move.class);
    }
    
    Mono<Void> notifyPlayer(Player player, Round round) {
        Result result = Optional.ofNullable(round.winner()).map(winner -> winner.equals(player) ? Result.WON : Result.LOST).orElse(Result.DRAW);

        int stake = Result.DRAW.equals(result) ? 0 : round.stake();
        
        return WebClient.create(player.clientUrl()).post().uri("/round/result/{playerId}", player.id()).bodyValue(new RoundResult(round.roundId(), result, stake)).retrieve().bodyToMono(Void.class);
    }
}
