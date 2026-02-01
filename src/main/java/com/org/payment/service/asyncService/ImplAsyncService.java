package com.org.payment.service.asyncService;

import com.org.payment.service.cacheService.PaymentStatusPollingCache;
import com.org.payment.model.entity.Payment;
import com.org.payment.model.enums.PaymentStatus;
import com.org.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImplAsyncService implements IntAsyncService {

    private static final Logger logger = LogManager.getLogger(ImplAsyncService.class);
    private final PaymentRepository paymentRepository;
    private final PaymentStatusPollingCache paymentStatusPollingCache;

    @Async("paymentTaskExecutor")
    @Transactional
    @Override
    public void processPayment(String paymentId) {

        logger.info("Async processing started for paymentId={}", paymentId);
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if (payment != null && payment.getStatus().isTerminal()) {
            logger.warn("Payment already terminal. Skipping async processing. paymentId={}, status={}", paymentId, payment.getStatus());
            return;
        }

        try {
            simulateExternalGatewayCall();

            boolean success = java.util.concurrent.ThreadLocalRandom.current().nextInt(100) < 80;
            assert payment != null;
            if (success) {
                payment.transitionTo(PaymentStatus.SUCCESS);
                logger.info("Payment SUCCESS for paymentId={}", paymentId);
                paymentStatusPollingCache.put(paymentId, PaymentStatus.SUCCESS);
            } else {
                payment.transitionTo(PaymentStatus.FAILED);
                logger.warn("Payment FAILED for paymentId={}", paymentId);
                paymentStatusPollingCache.put(paymentId, PaymentStatus.FAILED);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Async processing interrupted. paymentId={}", paymentId);
            return;
        } catch (Exception ex) {
            assert payment != null;
            payment.transitionTo(PaymentStatus.FAILED);
            paymentStatusPollingCache.put(paymentId, PaymentStatus.FAILED);
            logger.error("Unexpected error during payment processing. paymentId={}", paymentId, ex);
        }
        paymentRepository.save(payment);
    }


    private void simulateExternalGatewayCall() throws InterruptedException {
        Thread.sleep(3000);
    }
}
