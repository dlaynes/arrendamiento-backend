package com.grupo2is2.arrendamiento.repository;

import com.grupo2is2.arrendamiento.domain.Property;
import com.grupo2is2.arrendamiento.domain.PropertyStatus;
import com.grupo2is2.arrendamiento.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwner(User owner);
    List<Property> findByStatus(PropertyStatus status);

    @Query("SELECT p FROM Property p JOIN FETCH p.owner LEFT JOIN FETCH p.amenities WHERE p.id = :id")
    Optional<Property> findByIdWithOwner(Long id);

    @Query("SELECT DISTINCT p FROM Property p JOIN FETCH p.owner LEFT JOIN FETCH p.amenities")
    List<Property> findAllWithOwner();
}
