package com.example.orderingapp.webSocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.SecureRandom;
import java.util.List;

@Controller
public class WebSocketController {

    @MessageMapping("/server/test/socket")
    @SendTo("/client/test/socket")
    public String test () throws Exception {
        return List.of("Web Socket Test Success", "Web Socket Test Success 1", "Web Socket Test Success 2")
                .get(new SecureRandom().nextInt(3));
    }

}

