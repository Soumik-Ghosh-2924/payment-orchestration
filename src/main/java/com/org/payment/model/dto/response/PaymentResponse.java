package com.org.payment.model.dto.response;

import com.org.payment.model.entity.Payment;
import com.org.payment.model.enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentResponse {
    private String paymentId;
    private PaymentStatus status;
    private Long amount;

    public static PaymentResponse from(Payment payment) {
        PaymentResponse r = new PaymentResponse();
        r.paymentId = payment.getPaymentId();
        r.status = payment.getStatus();
        r.amount = payment.getAmount();
        return r;
    }
}
