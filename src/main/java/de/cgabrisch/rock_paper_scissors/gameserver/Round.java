package de.cgabrisch.rock_paper_scissors.gameserver;

import java.util.Optional;

import de.cgabrisch.rock_paper_scissors.api.player.Player;

public record Round(String roundId, Player player1, Player player2, Calls calls, int stake) {
    public Optional<Player> getWinner() {
        if (calls.playerOne().beats(calls.playerTwo())) {
            return Optional.of(player1);
        }
        
        if (calls.playerTwo().beats(calls.playerOne())) {
            return Optional.of(player2);
        }
        
        return Optional.empty();
    }
}