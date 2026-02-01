
package com.org.payment.model.dto.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private String userId;
    private Long amount;
    private Long receiverContactNumber;
    private String idempotencyKey;
    private String currency;
}
