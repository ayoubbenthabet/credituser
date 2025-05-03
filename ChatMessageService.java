package com.example.BACK.service;

import com.example.BACK.model.ChatMessage;
import com.example.BACK.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {

    @Autowired
    private SentimentService sentimentService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public void processMessageAndSave(Long senderId, Long receiverId, String content) {
        System.out.println("Sending message to Flask: " + content);

        String sentiment = sentimentService.getSentimentFromFlask(content);

        System.out.println("Received sentiment from Flask: " + sentiment);
    }

}
