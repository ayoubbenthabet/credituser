package com.example.BACK.controller;

import com.example.BACK.service.MessageAnalysisService;
import com.example.BACK.service.WebSocketSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private WebSocketSessionService webSocketSessionService;

    @Autowired
    private MessageAnalysisService messageAnalysisService;

    // Lorsqu'un message est envoyé sur WebSocket, il est reçu ici
    @MessageMapping("/sendMessage")
    @SendTo("/topic/group")
    public String handleMessage(Long senderId, Long receiverId, String message) {
        // Analyser et enregistrer le message
        messageAnalysisService.handleMessage(senderId, receiverId, message);

        // Retourner le message à tous les utilisateurs du groupe
        return message;
    }
}
