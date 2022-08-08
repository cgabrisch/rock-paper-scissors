package de.cgabrisch.rock_paper_scissors.gameserver;

import de.cgabrisch.rock_paper_scissors.shared.player.Player;

record Round(String roundId, Player player1, Player player2, Player winner, Calls calls, int stake) {
}
