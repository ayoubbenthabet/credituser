package com.example.BACK.controller;

import com.example.BACK.model.ChatMessage;
import com.example.BACK.service.WebSocketSessionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Controller
public class ChatController {

    private final WebSocketSessionService sessionService;

    public ChatController(WebSocketSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage message) throws IOException {
        Long senderId = message.getSenderId();
        Long receiverId = message.getReceiverId();

        String senderRole = sessionService.getUserRole(senderId);
        String receiverRole = sessionService.getUserRole(receiverId);

        // Vérification des rôles pour le transfert de message
        boolean canSend = false;
        if ("CLIENT".equals(senderRole) && "CLIENT".equals(receiverRole)) {
            canSend = true; // CLIENT -> CLIENT
        } else if ("AGENT".equals(senderRole) && "AGENT".equals(receiverRole)) {
            canSend = true; // AGENT -> AGENT
        } else if ("INVESTOR".equals(senderRole) && "INVESTOR".equals(receiverRole)) {
            canSend = true; // INVESTOR -> INVESTOR
        } else if ("AGENT".equals(senderRole) && "INVESTOR".equals(receiverRole)) {
            canSend = true; // AGENT -> INVESTOR
        } else if ("INVESTOR".equals(senderRole) && "AGENT".equals(receiverRole)) {
            canSend = true; // INVESTOR -> AGENT
        }

        if (!canSend) {
            System.out.println("Message bloqué : Rôles incompatibles pour l'envoi.");
            return; // Bloquer l'envoi du message si les rôles ne correspondent pas
        }

        // Envoyer le message si la condition est respectée
        WebSocketSession receiverSession = sessionService.getSession(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(message.getContent()));
        } else {
            System.out.println("Le destinataire n'est pas connecté.");
        }
    }

}
