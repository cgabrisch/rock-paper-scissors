package de.cgabrisch.rock_paper_scissors.gameserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
class GameServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Value("${game_server.port}")
    private int gameServerPort;
    
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(gameServerPort);
    }

}
