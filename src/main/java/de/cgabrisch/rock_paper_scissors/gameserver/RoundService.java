package de.cgabrisch.rock_paper_scissors.gameserver;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import de.cgabrisch.rock_paper_scissors.api.player.Player;
import de.cgabrisch.rock_paper_scissors.api.round.Move;
import de.cgabrisch.rock_paper_scissors.api.round.MoveRequest;
import de.cgabrisch.rock_paper_scissors.api.round.RoundResult;
import de.cgabrisch.rock_paper_scissors.api.round.RoundResult.Result;
import reactor.core.publisher.Mono;

@Service
public class RoundService {
    Mono<Move> getMoveFromPlayer(Player player, String roundId, String opponentName) {
        return WebClient.create(player.clientUrl()).post().uri("/round/call/{playerId}", player.id()).bodyValue(new MoveRequest(roundId, opponentName)).retrieve().bodyToMono(Move.class);
    }
    
    Mono<Void> notifyPlayer(Player player, Round round) {
        Result result = round.getWinner().map(winner -> winner.equals(player) ? Result.WON : Result.LOST).orElse(Result.DRAW);

        int stake = Result.DRAW.equals(result) ? 0 : round.stake();
        
        return WebClient.create(player.clientUrl()).post().uri("/round/result/{playerId}", player.id()).bodyValue(new RoundResult(round.roundId(), result, stake)).retrieve().bodyToMono(Void.class);
    }
}
