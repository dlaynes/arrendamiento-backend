package com.grupo2is2.arrendamiento.repository;

import com.grupo2is2.arrendamiento.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId AND m.seen = false AND m.sender.id != :userId")
    Long countUnreadByConversationAndUser(Long conversationId, Long userId);

    @Modifying
    @Query("UPDATE Message m SET m.seen = true WHERE m.conversation.id = :conversationId AND m.sender.id != :userId AND m.seen = false")
    int markAsRead(Long conversationId, Long userId);
}
