package com.org.payment.model.dto.cache;

import com.org.payment.model.dto.response.PaymentResponse;

public record CachedIdempotentRecord(
        String paymentId,
        String requestHash,
        PaymentResponse response
) {}
