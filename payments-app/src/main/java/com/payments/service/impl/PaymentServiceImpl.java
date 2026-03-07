package com.payments.service.impl;

import com.payments.entity.Payment;
import com.payments.mapper.PaymentMapper;
import com.payments.model.PaymentRequest;
import com.payments.repository.PaymentRepository;
import com.payments.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentMapper paymentMapper;
    private final PaymentRepository repository;

    @Override
    public com.payments.model.Payment makePayment(PaymentRequest paymentRequest) {
        log.debug("Payment request received.");
        Payment entity = paymentMapper.toEntity(paymentRequest);
        Payment savedPayment = repository.save(entity);
        log.debug("payment stored successful");
        return paymentMapper.toModel(savedPayment);
    }

    @Override
    public Optional<com.payments.model.Payment> getPaymentById(String paymentId) {
        return repository.findByPaymentId(paymentId).map(paymentMapper::toModel);
    }
}