
package com.org.payment.controller;

import com.org.payment.model.dto.request.PaymentRequest;
import com.org.payment.model.dto.response.PaymentResponse;
import com.org.payment.service.paymentService.PaymentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/Payment", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public PaymentResponse create(@RequestBody PaymentRequest request) {
        return service.createPayment(request);
    }
}
