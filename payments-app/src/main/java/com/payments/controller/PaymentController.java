package com.payments.controller;


import com.payments.api.PaymentsApi;
import com.payments.exception.PaymentNotFoundException;
import com.payments.model.Payment;
import com.payments.model.PaymentRequest;
import com.payments.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@Validated
@RequestMapping("/api/v1")
public class PaymentController implements PaymentsApi {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return ResponseEntity.ok(payment);
    }

    @Override
    public ResponseEntity<Payment> makePayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        Payment res = paymentService.makePayment(paymentRequest);
        URI location = URI.create("/api/payments/" + res.getId());
        return ResponseEntity.created(location).body(res);
    }
}

