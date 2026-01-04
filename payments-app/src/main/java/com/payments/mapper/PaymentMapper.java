package com.payments.mapper;

import com.payments.entity.PaymentEntity;
import com.payments.model.Party;
import com.payments.model.Payment;
import com.payments.model.PaymentRequest;
import com.payments.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMapper {

    public PaymentEntity toEntity(PaymentRequest request) {
        if (request == null) {
            return null;
        }
        PaymentEntity entity = new PaymentEntity();
        entity.setPaymentId(UUID.randomUUID().toString());
        entity.setUserName("Admin");
        entity.setAmount(request.getAmount());
        entity.setCurrency(request.getCurrency());
        entity.setPaymentMethod(request.getPaymentMethod());
        entity.setStatus(PaymentStatus.PENDING);

        // Map debtor details
        if (request.getDebtor() != null) {
            entity.setDebtorName(request.getDebtor().getName());
            entity.setDebtorAccountNumber(request.getDebtor().getAccountNumber());
            entity.setDebtorBankCode(request.getDebtor().getBankCode());
            entity.setDebtorAddress(request.getDebtor().getAddress());
            entity.setDebtorEmail(request.getDebtor().getEmail());
            entity.setDebtorPhoneNumber(request.getDebtor().getPhoneNumber());
        }

        // Map creditor details
        if (request.getCreditor() != null) {
            entity.setCreditorName(request.getCreditor().getName());
            entity.setCreditorAccountNumber(request.getCreditor().getAccountNumber());
            entity.setCreditorBankCode(request.getCreditor().getBankCode());
            entity.setCreditorAddress(request.getCreditor().getAddress());
            entity.setCreditorEmail(request.getCreditor().getEmail());
            entity.setCreditorPhoneNumber(request.getCreditor().getPhoneNumber());
        }

        return entity;
    }


    public Payment toModel(PaymentEntity entity) {
        if (entity == null) {
            return null;
        }
        Payment payment = new Payment();
        payment.setPaymentId(entity.getPaymentId());
        payment.setAmount(entity.getAmount());
        payment.setCurrency(entity.getCurrency());
        payment.setStatus(entity.getStatus());
        payment.setPaymentMethod(entity.getPaymentMethod());
        payment.setCreatedAt(entity.getCreatedAt());
        payment.setUpdatedAt(entity.getUpdatedAt());

        // Map debtor details
        if (entity.getDebtorName() != null) {
            Party debtor = new Party();
            debtor.setName(entity.getDebtorName());
            debtor.setAccountNumber(entity.getDebtorAccountNumber());
            debtor.setBankCode(entity.getDebtorBankCode());
            debtor.setAddress(entity.getDebtorAddress());
            debtor.setEmail(entity.getDebtorEmail());
            debtor.setPhoneNumber(entity.getDebtorPhoneNumber());
            payment.setDebtor(debtor);
        }

        // Map creditor details
        if (entity.getCreditorName() != null) {
            Party creditor = new Party();
            creditor.setName(entity.getCreditorName());
            creditor.setAccountNumber(entity.getCreditorAccountNumber());
            creditor.setBankCode(entity.getCreditorBankCode());
            creditor.setAddress(entity.getCreditorAddress());
            creditor.setEmail(entity.getCreditorEmail());
            creditor.setPhoneNumber(entity.getCreditorPhoneNumber());
            payment.setCreditor(creditor);
        }

        return payment;
    }
}