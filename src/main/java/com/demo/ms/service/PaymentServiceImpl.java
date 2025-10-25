package com.demo.ms.service;

import com.demo.ms.entity.PaymentEntity;
import com.demo.ms.mapper.PaymentMapper;
import com.demo.ms.model.Payment;
import com.demo.ms.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    public PaymentServiceImpl(PaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment createPayment(Payment payment) {
        PaymentEntity entity = PaymentMapper.toEntity(payment);
        entity.setId(null); // ensure DB-generated id
        PaymentEntity saved = repository.save(entity);
        return PaymentMapper.toModel(saved);
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return repository.findById(id).map(PaymentMapper::toModel);
    }
}