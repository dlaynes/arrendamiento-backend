package com.grupo2is2.arrendamiento.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ConversationDto {
    private Long id;
    private Long participantId;
    private String participantName;
    private String participantAvatar;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Long unreadCount;
}
