package com.payments.mapper;

import com.payments.entity.PaymentEntity;
import com.payments.model.Payment;
import com.payments.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentEntity toEntity(Payment model) {
        if (model == null) return null;
        PaymentEntity e = new PaymentEntity();
        e.setId(model.getId());
        e.setTransactionId(UUID.randomUUID().toString());
        e.setAmount(model.getAmount());
        e.setCurrency(model.getCurrency());
        e.setStatus(model.getStatus());
        e.setPaymentMethod(model.getPaymentMethod());
        e.setStatus(PaymentStatus.PENDING);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());
        return e;
    }

    public static Payment toModel(PaymentEntity entity) {
        if (entity == null) return null;
        Payment m = new Payment();
        m.setId(entity.getId());
        m.setAmount(entity.getAmount());
        m.setCurrency(entity.getCurrency());
        m.setStatus(entity.getStatus());
        m.setPaymentMethod(entity.getPaymentMethod());
        m.setCreatedAt(entity.getCreatedAt());
        m.setUpdatedAt(entity.getUpdatedAt());
        return m;
    }
}