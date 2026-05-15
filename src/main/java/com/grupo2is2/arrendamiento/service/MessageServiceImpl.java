package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.domain.Conversation;
import com.grupo2is2.arrendamiento.domain.Message;
import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.dto.MessageDto;
import com.grupo2is2.arrendamiento.repository.ConversationRepository;
import com.grupo2is2.arrendamiento.repository.MessageRepository;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getMessages(Long conversationId, Long userId) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));
        validateParticipant(conv, userId);

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageDto sendMessage(Long conversationId, Long senderId, String content) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));
        validateParticipant(conv, senderId);

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Message message = Message.builder()
                .conversation(conv)
                .sender(sender)
                .content(content)
                .seen(false)
                .build();

        Message saved = messageRepository.save(message);

        // Update conversation timestamp
        conv.setUpdatedAt(java.time.LocalDateTime.now());
        conversationRepository.save(conv);

        return toDto(saved);
    }

    @Override
    @Transactional
    public void markAsRead(Long conversationId, Long userId) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));
        validateParticipant(conv, userId);
        messageRepository.markAsRead(conversationId, userId);
    }

    private void validateParticipant(Conversation conv, Long userId) {
        boolean isParticipant = conv.getUser1().getId().equals(userId) || conv.getUser2().getId().equals(userId);
        if (!isParticipant) {
            throw new RuntimeException("No tienes acceso a esta conversación");
        }
    }

    private MessageDto toDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .content(message.getContent())
                .timestamp(message.getCreatedAt())
                .read(message.getSeen())
                .build();
    }
}
