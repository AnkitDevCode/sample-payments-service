package com.payments.service;

import com.payments.model.Payment;
import com.payments.model.PaymentRequest;

import java.util.Optional;

public interface PaymentService {

    Payment makePayment(PaymentRequest paymentRequest);

    Optional<Payment> getPaymentById(String paymentId);
}