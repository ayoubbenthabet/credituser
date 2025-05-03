package com.example.BACK.service;

import com.example.BACK.model.ChatMessage;
import com.example.BACK.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageAnalysisService {

    @Autowired
    private SentimentService sentimentService;  // Service d'analyse des sentiments

    @Autowired
    private ChatMessageRepository chatMessageRepository; // Repository pour sauvegarder les messages

    public void handleMessage(Long senderId, Long receiverId, String content) {
        // Créer un objet ChatMessage
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(senderId);
        chatMessage.setReceiverId(receiverId);
        chatMessage.setContent(content);

        // Analyser le sentiment du message via Flask
        String sentiment = sentimentService.getSentimentFromFlask(content);  // Analyse de sentiment via Flask
        chatMessage.setSentiment(sentiment);
        chatMessage.setAnalyzed(true);  // Marquer comme analysé

        // Enregistrer le message dans la base de données
        chatMessageRepository.save(chatMessage);
    }

    public String analyzeSentiment(String message) {
        return sentimentService.getSentimentFromFlask(message);
    }
}
