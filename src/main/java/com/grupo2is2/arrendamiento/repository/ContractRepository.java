package com.grupo2is2.arrendamiento.repository;

import com.grupo2is2.arrendamiento.domain.Contract;
import com.grupo2is2.arrendamiento.domain.ContractStatus;
import com.grupo2is2.arrendamiento.domain.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByProperty(Property property);
    List<Contract> findByStatus(ContractStatus status);
    List<Contract> findByTenant(String tenant);
    List<Contract> findByPropertyOwnerId(Long ownerId);
}