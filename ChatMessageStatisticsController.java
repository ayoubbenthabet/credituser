package com.example.BACK.controller;

import com.example.BACK.service.ChatMessageStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatMessageStatisticsController {

    @Autowired
    private ChatMessageStatisticsService chatMessageStatisticsService;

    @GetMapping("/statistics/{agentId}")
    public String getStatistics(@PathVariable Long agentId) {
        return chatMessageStatisticsService.getMessageSentimentStatistics(agentId);
    }
}
