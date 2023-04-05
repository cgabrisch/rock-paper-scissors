package de.cgabrisch.rock_paper_scissors.botserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
class BotServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Value("${bot_server.port}")
    private int botServerPort;
    
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(botServerPort);
    }

}
