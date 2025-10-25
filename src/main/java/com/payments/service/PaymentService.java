package com.payments.service;

import com.payments.model.Payment;

import java.util.Optional;

public interface PaymentService {

    Payment makePayment(Payment payment);

    Optional<Payment> getPaymentById(Long id);
}