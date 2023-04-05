package de.cgabrisch.rock_paper_scissors.playerregistry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
class PlayerRegistryCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Value("${player_registry.port}")
    private int playerRegistryPort;
    
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(playerRegistryPort);
    }

}
