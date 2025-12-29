package com.payments.service;

import com.payments.dto.UserDto;
import com.payments.entity.PaymentEntity;
import com.payments.mapper.PaymentMapper;
import com.payments.model.Payment;
import com.payments.model.PaymentRequest;
import com.payments.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final JsonPlaceholderService jsonPlaceholderService;

    @Override
    public Payment makePayment(PaymentRequest paymentRequest) {
        log.info("Payment request received. {}", paymentRequest);
        //call getUser() show integration
        String userName = getUserName();

        PaymentEntity entity = PaymentMapper.toEntity(paymentRequest);

        entity.setUserName(userName);
        entity.setId(null); // ensure DB-generated id

        PaymentEntity saved = repository.save(entity);

        log.info("payment successful. {}", saved);
        return PaymentMapper.toModel(saved);
    }

    private String getUserName() {
        log.info("getUserName call started.");
        List<UserDto> userDtos = jsonPlaceholderService.fetchUsers();
        String userName = Optional.ofNullable(userDtos)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .map(UserDto::name)
                .orElse(null);
        log.info("getUserName call successful. firstUserName={}", userName);
        return userName;
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return repository.findById(id).map(PaymentMapper::toModel);
    }
}