package com.payments.controller;


import com.payments.exception.PaymentNotFoundException;
import com.payments.model.Payment;
import com.payments.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return ResponseEntity.ok(payment);
    }

    @PostMapping
    public ResponseEntity<Payment> makePayment(@RequestBody Payment payment) {
        Payment res = paymentService.makePayment(payment);
        URI location = URI.create("/api/payments/" + res.getId());
        return ResponseEntity.created(location).body(res);
    }
}

