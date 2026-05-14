package com.grupo2is2.arrendamiento.repository;

import com.grupo2is2.arrendamiento.domain.Contract;
import com.grupo2is2.arrendamiento.domain.Payment;
import com.grupo2is2.arrendamiento.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByContract(Contract contract);
    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p JOIN FETCH p.tenant JOIN FETCH p.contract WHERE p.id = :id")
    Optional<Payment> findByIdWithUser(Long id);

    @Query("SELECT p FROM Payment p JOIN FETCH p.tenant JOIN FETCH p.contract")
    List<Payment> findAllWithUser();

    @Query("SELECT p FROM Payment p JOIN FETCH p.tenant JOIN FETCH p.contract WHERE p.tenant.id = :tenantId")
    List<Payment> findByTenantIdWithUser(Long tenantId);

    @Query("SELECT p FROM Payment p JOIN FETCH p.tenant t JOIN FETCH p.contract c JOIN FETCH c.property prop WHERE prop.owner.id = :ownerId")
    List<Payment> findByContractPropertyOwnerId(Long ownerId);
}
