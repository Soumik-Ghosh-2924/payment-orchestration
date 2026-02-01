
package com.org.payment.repository;

import com.org.payment.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByPaymentId(String paymentId);
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
