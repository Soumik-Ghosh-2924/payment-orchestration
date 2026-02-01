package com.org.payment.controller;

import com.org.payment.service.cacheService.PaymentStatusPollingCache;
import com.org.payment.model.entity.Payment;
import com.org.payment.model.enums.PaymentStatus;
import com.org.payment.model.dto.cache.PaymentStatusResponse;
import com.org.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/Payment")
@RequiredArgsConstructor
public class PaymentQueryController {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusPollingCache paymentStatusPollingCache;
    private final Logger logger= LogManager.getLogger(PaymentQueryController.class);


    @GetMapping("/{paymentId}")
    public PaymentStatusResponse getPaymentStatus(@PathVariable String paymentId) {

        PaymentStatus cachedStatus = paymentStatusPollingCache.get(paymentId);
        if(cachedStatus!=null){
            return new PaymentStatusResponse(paymentId, cachedStatus);
        }

        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if(payment==null){
            logger.info("Payment isn't available !");
            return new PaymentStatusResponse("Payment_ID Unavailable",PaymentStatus.INDETERMINISTIC);
        }else {
            // fallback → populate cache
            paymentStatusPollingCache.put(paymentId, payment.getStatus());

        }

        return new PaymentStatusResponse(payment.getPaymentId(), payment.getStatus());
    }
}

