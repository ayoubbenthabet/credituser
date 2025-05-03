package com.example.BACK.repository;

import com.example.BACK.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByReceiverId(Long receiverId);
    List<ChatMessage> findByReceiverIdAndAnalyzedTrue(Long receiverId);

}
