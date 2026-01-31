package com.payments.service.impl;

import com.payments.entity.PaymentEntity;
import com.payments.mapper.PaymentMapper;
import com.payments.model.Payment;
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
    public Payment makePayment(PaymentRequest paymentRequest) {
        log.debug("Payment request received.");
        PaymentEntity entity = paymentMapper.toEntity(paymentRequest);
        PaymentEntity savedPayment = repository.save(entity);
        log.debug("payment stored successful");
        return paymentMapper.toModel(savedPayment);
    }

    @Override
    public Optional<Payment> getPaymentById(String paymentId) {
        return repository.findByPaymentId(paymentId).map(paymentMapper::toModel);
    }
}