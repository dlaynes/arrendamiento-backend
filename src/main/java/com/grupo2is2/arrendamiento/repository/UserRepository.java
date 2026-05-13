package com.grupo2is2.arrendamiento.repository;

import com.grupo2is2.arrendamiento.domain.User;
import com.grupo2is2.arrendamiento.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
}
