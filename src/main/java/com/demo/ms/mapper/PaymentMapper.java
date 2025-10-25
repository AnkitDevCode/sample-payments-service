package com.demo.ms.mapper;

import com.demo.ms.entity.PaymentEntity;
import com.demo.ms.model.Payment;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentEntity toEntity(Payment model) {
        if (model == null) return null;
        PaymentEntity e = new PaymentEntity();
        e.setId(model.getId());
        e.setAmount(model.getAmount());
        e.setCurrency(model.getCurrency());
        e.setStatus(model.getStatus());
        return e;
    }

    public static Payment toModel(PaymentEntity entity) {
        if (entity == null) return null;
        Payment m = new Payment();
        m.setId(entity.getId());
        m.setAmount(entity.getAmount());
        m.setCurrency(entity.getCurrency());
        m.setStatus(entity.getStatus());
        return m;
    }
}