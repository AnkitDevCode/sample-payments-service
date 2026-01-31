package com.payments.controller;


import com.payments.api.PaymentsApi;
import com.payments.exception.PaymentNotFoundException;
import com.payments.hateos.PaymentHateosBuilder;
import com.payments.model.Payment;
import com.payments.model.PaymentRequest;
import com.payments.service.PaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Validated
@RequestMapping
@AllArgsConstructor
public class PaymentController implements PaymentsApi {
    private final PaymentService paymentService;
    private final PaymentHateosBuilder paymentHateosBuilder;

    @Override
    public ResponseEntity<Payment> getPaymentById(@PathVariable String paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        return ResponseEntity.ok(paymentHateosBuilder.addLinks(payment));
    }

    @Override
    public ResponseEntity<Payment> makePayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        Payment res = paymentService.makePayment(paymentRequest);
        Payment paymentWithLinks = paymentHateosBuilder.addLinks(res);
        URI location = paymentHateosBuilder.buildLocationUri(res.getPaymentId());
        return ResponseEntity.created(location).body(paymentWithLinks);
    }
}

