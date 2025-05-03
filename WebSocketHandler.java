package com.example.BACK.config;

import com.example.BACK.model.ChatMessage;
import com.example.BACK.model.Role;
import com.example.BACK.model.User;
import com.example.BACK.repository.ChatMessageRepository;
import com.example.BACK.repository.UserRepository;
import com.example.BACK.service.MessageAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<Long, String> userRoles = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageAnalysisService messageAnalysisService;

    @Autowired
    public WebSocketHandler(UserRepository userRepository,
                            ChatMessageRepository chatMessageRepository,
                            MessageAnalysisService messageAnalysisService) {
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.messageAnalysisService = messageAnalysisService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = extractUserId(session);
        if (userId != null) {
            String userRole = getUserRoleFromDatabase(userId);
            if (userRole == null) {
                session.sendMessage(new TextMessage("Error: User not found!"));
                session.close();
                return;
            }

            sessions.put(userId, session);
            userRoles.put(userId, userRole);
            session.sendMessage(new TextMessage("Bienvenue ! Votre rôle est : " + userRole));
            System.out.println("User " + userId + " connecté avec le rôle : " + userRole);
        } else {
            session.close();
        }
    }

    private Long extractUserId(WebSocketSession session) {
        try {
            String path = session.getUri().getPath();
            String[] segments = path.split("/");
            return Long.parseLong(segments[segments.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }

    private String getUserRoleFromDatabase(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(u -> {
            Role role = u.getRole();
            return (role != null) ? role.getNomRole() : "UNKNOWN";
        }).orElse(null);
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        for (Map.Entry<Long, WebSocketSession> entry : sessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            String[] msgParts = payload.split(":", 2);

            if (msgParts.length < 2) {
                System.out.println("Message mal formaté. Format attendu : <receiverEmail>:<content>. Reçu : " + payload);
                return;
            }

            String receiverEmail = msgParts[0];  // Email du destinataire
            String content = msgParts[1];

            // Récupérer l'expéditeur
            Long senderId = getUserIdFromSession(session);
            if (senderId == null) {
                System.out.println("Impossible d'identifier l'expéditeur.");
                return;
            }

            User sender = userRepository.findById(senderId).orElse(null);
            if (sender == null) {
                System.out.println("Expéditeur introuvable avec l'ID : " + senderId);
                return;
            }

            // Récupérer le destinataire
            User receiver = userRepository.findByEmail(receiverEmail);
            if (receiver == null) {
                System.out.println("Destinataire introuvable avec l'email : " + receiverEmail);
                return;
            }

            // Récupérer les rôles
            String senderRole = sender.getRole().getNomRole();
            String receiverRole = receiver.getRole().getNomRole();

            // DEBUG : Vérifier qui envoie à qui
            System.out.println("DEBUG - Sender: " + sender.getEmail() + " (" + senderRole + ") -> Receiver: " + receiver.getEmail() + " (" + receiverRole + ")");

            // Bloquer INVESTOR ↔ INVESTOR
            if ("INVESTOR".equals(senderRole) && "INVESTOR".equals(receiverRole)) {
                System.out.println("Message bloqué ❌ : Communication INVESTOR ↔ INVESTOR interdite !");
                return;
            }

            // Vérifier les rôles autorisés
            boolean canSend = ("CLIENT".equals(senderRole) && "CLIENT".equals(receiverRole))
                    || ("AGENT".equals(senderRole) && "INVESTOR".equals(receiverRole))
                    || ("INVESTOR".equals(senderRole) && "AGENT".equals(receiverRole));

            if (!canSend) {
                System.out.println("Message bloqué ❌ : Communication interdite entre " + senderRole + " et " + receiverRole);
                return;
            }

            // Enregistrer le message dans la base de données
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderId(sender.getId());  // Utiliser l'ID de l'expéditeur
            chatMessage.setReceiverId(receiver.getId());  // Utiliser l'ID du destinataire
            chatMessage.setContent(content);
            chatMessage.setAnalyzed(false);  // Ce champ sera mis à jour après l'analyse

            // Enregistrer le message avant l'analyse
            chatMessageRepository.save(chatMessage);

            // Analyser le message (par exemple, avec un SVM ou un arbre de décision)
            String sentiment = messageAnalysisService.analyzeSentiment(content);
            chatMessage.setSentiment(sentiment);
            chatMessage.setAnalyzed(true);  // Mettre à jour après analyse

            // Mettre à jour le message dans la base de données après l'analyse
            chatMessageRepository.save(chatMessage);

            // Formatage du message
            String formattedMessage = sender.getEmail() + ":" + content;

            // Envoi du message si le destinataire est connecté
            WebSocketSession receiverSession = sessions.get(receiver.getId());
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.sendMessage(new TextMessage(formattedMessage));
            } else {
                System.out.println("Le destinataire n'est pas connecté.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi du message.");
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = extractUserId(session);
        if (userId != null) {
            sessions.remove(userId);
            userRoles.remove(userId);
            System.out.println("User " + userId + " déconnecté.");
        }
    }
}
