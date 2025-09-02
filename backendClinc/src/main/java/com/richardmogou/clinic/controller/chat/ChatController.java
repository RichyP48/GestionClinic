package com.richardmogou.clinic.controller.chat;

import com.richardmogou.clinic.model.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handles messages sent to /app/chat.public
     * Broadcasts the message to all subscribers of /topic/public
     */
    @MessageMapping("/chat.public")
    @SendTo("/topic/public")
    public ChatMessage handlePublicMessage(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor,
            Principal principal
    ) {
        if (chatMessage.getSender() == null) {
            chatMessage.setSender(principal.getName());
        }
        
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(Instant.now());
        }

        return chatMessage;
    }

    /**
     * Handles messages sent to /app/chat.private
     * Sends the message to the specific user's private queue
     */
    @MessageMapping("/chat.private")
    public void handlePrivateMessage(
            @Payload ChatMessage chatMessage,
            Principal principal
    ) {
        if (chatMessage.getSender() == null) {
            chatMessage.setSender(principal.getName());
        }

        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(Instant.now());
        }

        if (chatMessage.getRecipient() == null || chatMessage.getRecipient().trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient is required for private messages");
        }

        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient(),
                "/queue/private",
                chatMessage
        );

        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/private",
                chatMessage
        );
    }

    /**
     * Handles user join events
     */
    @MessageMapping("/chat.join")
    @SendTo("/topic/public")
    public ChatMessage handleUserJoin(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor,
            Principal principal
    ) {
        headerAccessor.getSessionAttributes().put("username", principal.getName());

        return ChatMessage.builder()
                .type(ChatMessage.MessageType.JOIN)
                .sender(principal.getName())
                .content(principal.getName() + " joined the chat")
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Handles user leave events
     */
    @MessageMapping("/chat.leave")
    @SendTo("/topic/public")
    public ChatMessage handleUserLeave(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor,
            Principal principal
    ) {
        return ChatMessage.builder()
                .type(ChatMessage.MessageType.LEAVE)
                .sender(principal.getName())
                .content(principal.getName() + " left the chat")
                .timestamp(Instant.now())
                .build();
    }
}