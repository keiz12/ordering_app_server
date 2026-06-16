package com.example.orderingapp.config;

import org.springframework.context.annotation.*;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/client"); // where messages are sent
        config.setApplicationDestinationPrefixes("/app"); // not used here, only for sending from client
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/all/server_connect") // client connects here
                .setAllowedOriginPatterns("*");
    }
}
