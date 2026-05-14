package com.grupo2is2.arrendamiento.repository;

import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    @Query(value = "SELECT DISTINCT u.* FROM users u JOIN contracts c ON c.tenant_id = u.id JOIN properties p ON c.property_id = p.id WHERE p.owner_id = :ownerId AND u.role = 'INQUILINO'", nativeQuery = true)
    List<User> findTenantsByPropertyOwnerId(@Param("ownerId") Long ownerId);
}
