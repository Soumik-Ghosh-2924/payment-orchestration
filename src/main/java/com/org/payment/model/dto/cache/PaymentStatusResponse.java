package com.org.payment.model.dto.cache;

import com.org.payment.model.enums.PaymentStatus;

public record PaymentStatusResponse(
        String paymentId,
        PaymentStatus status
) {}
