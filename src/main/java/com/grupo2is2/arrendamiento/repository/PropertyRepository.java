package com.grupo2is2.arrendamiento.repository;

import com.grupo2is2.arrendamiento.domain.Property;
import com.grupo2is2.arrendamiento.domain.PropertyStatus;
import com.grupo2is2.arrendamiento.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwner(User owner);
    List<Property> findByStatus(PropertyStatus status);
    List<Property> findByTenant(String tenant);
}