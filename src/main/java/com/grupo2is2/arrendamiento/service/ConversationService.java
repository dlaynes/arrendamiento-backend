package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.dto.ConversationDto;
import java.util.List;

public interface ConversationService {
    ConversationDto getOrCreateConversation(Long userId, Long otherUserId);
    List<ConversationDto> getMyConversations(Long userId);
    ConversationDto getConversationById(Long id, Long userId);
}
