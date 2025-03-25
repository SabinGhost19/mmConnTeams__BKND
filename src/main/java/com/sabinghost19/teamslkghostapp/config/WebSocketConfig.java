package com.sabinghost19.teamslkghostapp.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefixele pentru canalele la care clienții pot primi mesaje
        config.enableSimpleBroker("/topic", "/queue", "/user");

        // Prefixul pentru endpoint-uri la care clienții pot trimite mesaje
        config.setApplicationDestinationPrefixes("/app");

        // Configurare pentru mesaje directe către utilizatori specifici
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint-ul WebSocket - clienții se conectează aici
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // În producție, specificați doar originile permise
                .withSockJS();  // Suport SockJS pentru browsere care nu au WebSocket nativ
    }
}