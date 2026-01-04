package com.payments.entity;


import com.payments.model.PaymentMethod;
import com.payments.model.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "debtor_name")
    private String debtorName;

    @Column(name = "debtor_account_number")
    private String debtorAccountNumber;

    @Column(name = "debtor_bank_code")
    private String debtorBankCode;

    @Column(name = "debtor_address", length = 500)
    private String debtorAddress;

    @Column(name = "debtor_email")
    private String debtorEmail;

    @Column(name = "debtor_phone_number")
    private String debtorPhoneNumber;

    @Column(name = "creditor_name")
    private String creditorName;

    @Column(name = "creditor_account_number")
    private String creditorAccountNumber;

    @Column(name = "creditor_bank_code")
    private String creditorBankCode;

    @Column(name = "creditor_address", length = 500)
    private String creditorAddress;

    @Column(name = "creditor_email")
    private String creditorEmail;

    @Column(name = "creditor_phone_number")
    private String creditorPhoneNumber;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}