package com.grupo2is2.arrendamiento.controller;

import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.dto.ConversationDto;
import com.grupo2is2.arrendamiento.dto.MessageDto;
import com.grupo2is2.arrendamiento.dto.SendMessageRequest;
import com.grupo2is2.arrendamiento.dto.StartConversationRequest;
import com.grupo2is2.arrendamiento.repository.UserRepository;
import com.grupo2is2.arrendamiento.service.ConversationService;
import com.grupo2is2.arrendamiento.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class MessageController {

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<List<ConversationDto>> getMyConversations() {
        return ResponseEntity.ok(conversationService.getMyConversations(getCurrentUserId()));
    }

    @PostMapping
    public ResponseEntity<ConversationDto> startConversation(@Valid @RequestBody StartConversationRequest request) {
        return ResponseEntity.ok(conversationService.getOrCreateConversation(getCurrentUserId(), request.getOtherUserId()));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getMessages(id, getCurrentUserId()));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageDto> sendMessage(@PathVariable Long id, @Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(id, getCurrentUserId(), request.getContent()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
