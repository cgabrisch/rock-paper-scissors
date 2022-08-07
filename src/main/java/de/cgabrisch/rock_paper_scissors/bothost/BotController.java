package de.cgabrisch.rock_paper_scissors.bothost;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import de.cgabrisch.rock_paper_scissors.api.player.PlayerId;
import de.cgabrisch.rock_paper_scissors.api.player.PlayerRegistration;
import reactor.core.publisher.Mono;

@RestController
public class BotController {
    @Value("${rps_server.url}")
    private String rpsServerUrl;
    
    @Value("${own_server.url}")
    private String ownUrl;
    
    @PostMapping("/bots")
    Mono<PlayerId> newBot(@RequestParam("name") String name) {
        return WebClient.create(rpsServerUrl + "/players").post().bodyValue(new PlayerRegistration(name, ownUrl)).retrieve().bodyToMono(PlayerId.class);
    }
}
