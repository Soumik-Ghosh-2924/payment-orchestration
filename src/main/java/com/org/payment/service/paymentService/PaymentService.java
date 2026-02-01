
package com.org.payment.service.paymentService;

import com.org.payment.service.cacheService.IdempotencyCache;
import com.org.payment.service.cacheService.PaymentStatusPollingCache;
import com.org.payment.model.entity.Payment;
import com.org.payment.model.enums.PaymentStatus;
import com.org.payment.model.dto.cache.CachedIdempotentRecord;
import com.org.payment.model.dto.request.PaymentRequest;
import com.org.payment.exception.IdempotencyConflictException;
import com.org.payment.model.dto.response.PaymentResponse;
import com.org.payment.repository.PaymentRepository;
import com.org.payment.service.asyncService.ImplAsyncService;
import com.org.payment.util.RequestHashUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final ImplAsyncService asyncService;
    private final PaymentStatusPollingCache paymentStatusPollingCache;
    private final IdempotencyCache idempotencyCache;

    private final Logger logger= LogManager.getLogger(PaymentService.class);

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {

        String idempotencyKey = request.getIdempotencyKey();
        String requestHash = RequestHashUtil.hash(RequestHashUtil.canonicalize(request));

        CachedIdempotentRecord cached = idempotencyCache.get(idempotencyKey);
        if (cached != null) {
            if (!cached.requestHash().equals(requestHash)) {
                logger.warn("ALERT ! Same Idempotency Key being used.");
                throw new IdempotencyConflictException(
                        "Same idempotency key used with different payload"
                );
            }
            logger.info("Idempotency cache hit");
            return cached.response();
        } else {

            Optional<Payment> existing = repository.findByIdempotencyKey(idempotencyKey);

            if (existing.isPresent()) {
                Payment payment = existing.get();
                if (!Objects.equals(payment.getRequestHash(), requestHash)) {
                    logger.warn("ALERT ! Same Idempotency Key being used.");
                    throw new IdempotencyConflictException(
                            "Same idempotency key used with different payload"
                    );
                }
                PaymentResponse response = PaymentResponse.from(payment);
                idempotencyCache.put(idempotencyKey, new CachedIdempotentRecord(payment.getPaymentId(), requestHash, response));

                return response;
            }
        }


        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setIdempotencyKey(idempotencyKey);
        payment.setRequestHash(requestHash);
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setUserId(request.getUserId());
        repository.save(payment);
        payment.transitionTo(PaymentStatus.PROCESSING);
        repository.saveAndFlush(payment);

        paymentStatusPollingCache.put(payment.getPaymentId(), PaymentStatus.PROCESSING);

        PaymentResponse response = PaymentResponse.from(payment);

        idempotencyCache.put(idempotencyKey, new CachedIdempotentRecord(payment.getPaymentId(), requestHash, response));

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        asyncService.processPayment(payment.getPaymentId());
                    }
                }
        );

        return response;
    }
}
