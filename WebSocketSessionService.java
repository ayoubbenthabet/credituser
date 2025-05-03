package com.example.BACK.service;

import com.example.BACK.model.ChatMessage;
import com.example.BACK.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSessionService {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<Long, String> userRoles = new ConcurrentHashMap<>();

    @Autowired
    private SentimentService sentimentService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public void addSession(Long userId, WebSocketSession session, String role) {
        sessions.put(userId, session);
        userRoles.put(userId, role);
    }

    public void removeSession(Long userId) {
        sessions.remove(userId);
        userRoles.remove(userId);
    }

    public WebSocketSession getSession(Long userId) {
        return sessions.get(userId);
    }

    public String getUserRole(Long userId) {
        return userRoles.get(userId);
    }

    public void handleMessage(Long senderId, Long receiverId, String message) {
        // Créer et analyser le message
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(senderId);
        chatMessage.setReceiverId(receiverId);
        chatMessage.setContent(message);

        // Analyser le sentiment via Flask (Assurez-vous que cette méthode fonctionne)
        String sentiment = sentimentService.getSentimentFromFlask(message);
        chatMessage.setSentiment(sentiment);
        chatMessage.setAnalyzed(true);  // Marquer comme analysé

        // Sauvegarder le message dans la base de données
        chatMessageRepository.save(chatMessage);
    }
}
