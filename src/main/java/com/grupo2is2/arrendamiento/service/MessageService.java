package com.grupo2is2.arrendamiento.service;

import com.grupo2is2.arrendamiento.dto.MessageDto;
import java.util.List;

public interface MessageService {
    List<MessageDto> getMessages(Long conversationId, Long userId);
    MessageDto sendMessage(Long conversationId, Long senderId, String content);
    void markAsRead(Long conversationId, Long userId);
}
