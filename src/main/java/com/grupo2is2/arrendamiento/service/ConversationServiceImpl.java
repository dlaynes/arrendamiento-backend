package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.domain.Conversation;
import com.grupo2is2.arrendamiento.domain.Message;
import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.dto.ConversationDto;
import com.grupo2is2.arrendamiento.repository.ConversationRepository;
import com.grupo2is2.arrendamiento.repository.MessageRepository;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ConversationDto getOrCreateConversation(Long userId, Long otherUserId) {
        if (userId.equals(otherUserId)) {
            throw new RuntimeException("No puedes iniciar una conversación contigo mismo");
        }

        // Enforce ordering: smaller ID is user1
        Long u1 = Math.min(userId, otherUserId);
        Long u2 = Math.max(userId, otherUserId);

        return conversationRepository.findByUserPair(u1, u2)
                .map(c -> toDto(c, userId))
                .orElseGet(() -> {
                    User user1Entity = userRepository.findById(u1)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                    User user2Entity = userRepository.findById(u2)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                    Conversation conv = Conversation.builder()
                            .user1(user1Entity)
                            .user2(user2Entity)
                            .build();
                    Conversation saved = conversationRepository.save(conv);
                    return toDto(saved, userId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationDto> getMyConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findByParticipantId(userId);
        return conversations.stream()
                .map(c -> toDto(c, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDto getConversationById(Long id, Long userId) {
        Conversation conv = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));
        validateParticipant(conv, userId);
        return toDto(conv, userId);
    }

    private void validateParticipant(Conversation conv, Long userId) {
        boolean isParticipant = conv.getUser1().getId().equals(userId) || conv.getUser2().getId().equals(userId);
        if (!isParticipant) {
            throw new RuntimeException("No tienes acceso a esta conversación");
        }
    }

    private ConversationDto toDto(Conversation conv, Long currentUserId) {
        User other = conv.getUser1().getId().equals(currentUserId) ? conv.getUser2() : conv.getUser1();

        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conv.getId());
        Message lastMessage = messages.isEmpty() ? null : messages.get(messages.size() - 1);
        Long unreadCount = messageRepository.countUnreadByConversationAndUser(conv.getId(), currentUserId);

        return ConversationDto.builder()
                .id(conv.getId())
                .participantId(other.getId())
                .participantName(other.getName())
                .participantAvatar(other.getAvatar())
                .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                .lastMessageAt(lastMessage != null ? lastMessage.getCreatedAt() : conv.getUpdatedAt())
                .unreadCount(unreadCount)
                .build();
    }
}
