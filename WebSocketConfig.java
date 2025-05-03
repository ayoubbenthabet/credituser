package com.example.BACK.config;

import com.example.BACK.service.WebSocketSessionService;
import com.example.BACK.service.MessageAnalysisService;
import com.example.BACK.repository.ChatMessageRepository;
import com.example.BACK.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketSessionService webSocketSessionService;
    private final MessageAnalysisService messageAnalysisService;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    public WebSocketConfig(WebSocketSessionService webSocketSessionService,
                           MessageAnalysisService messageAnalysisService,
                           UserRepository userRepository,
                           ChatMessageRepository chatMessageRepository) {
        this.webSocketSessionService = webSocketSessionService;
        this.messageAnalysisService = messageAnalysisService;
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(userRepository, chatMessageRepository, messageAnalysisService), "/ws/{userId}")
                .setAllowedOrigins("*");
    }
}
