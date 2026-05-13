package com.grupo2is2.arrendamiento.repository;

import com.grupo2is2.arrendamiento.domain.Contract;
import com.grupo2is2.arrendamiento.domain.Payment;
import com.grupo2is2.arrendamiento.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByContract(Contract contract);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByTenant(String tenant);
}
