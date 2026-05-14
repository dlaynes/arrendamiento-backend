package com.grupo2is2.arrendamiento.repository;

import com.grupo2is2.arrendamiento.domain.Contract;
import com.grupo2is2.arrendamiento.domain.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query("SELECT c FROM Contract c LEFT JOIN FETCH c.tenant JOIN FETCH c.landlord JOIN FETCH c.property WHERE c.id = :id")
    Optional<Contract> findByIdWithUsers(Long id);

    @Query("SELECT c FROM Contract c LEFT JOIN FETCH c.tenant JOIN FETCH c.landlord JOIN FETCH c.property")
    List<Contract> findAllWithUsers();

    @Query("SELECT c FROM Contract c LEFT JOIN FETCH c.tenant JOIN FETCH c.landlord JOIN FETCH c.property WHERE c.status = :status")
    List<Contract> findByStatusWithUsers(ContractStatus status);

    @Query("SELECT c FROM Contract c JOIN FETCH c.tenant JOIN FETCH c.landlord JOIN FETCH c.property WHERE c.tenant.id = :tenantId")
    List<Contract> findByTenantIdWithUsers(Long tenantId);

    @Query("SELECT c FROM Contract c LEFT JOIN FETCH c.tenant JOIN FETCH c.landlord JOIN FETCH c.property WHERE c.landlord.id = :landlordId")
    List<Contract> findByLandlordIdWithUsers(Long landlordId);

    @Query("SELECT c FROM Contract c LEFT JOIN FETCH c.tenant JOIN FETCH c.landlord JOIN FETCH c.property p WHERE p.id = :propertyId")
    List<Contract> findByPropertyIdWithUsers(Long propertyId);

    @Query("SELECT c FROM Contract c LEFT JOIN FETCH c.tenant JOIN FETCH c.landlord JOIN FETCH c.property p WHERE p.owner.id = :ownerId")
    List<Contract> findByPropertyOwnerIdWithUsers(Long ownerId);
}
