package com.demo.ms.service;

import com.demo.ms.model.Payment;

import java.util.Optional;

public interface PaymentService {
    Payment createPayment(Payment payment);

    Optional<Payment> getPaymentById(Long id);
}