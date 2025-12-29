package com.payments.service;

import com.payments.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class JsonPlaceholderService {

    private final WebClient webClient;

    public List<UserDto> fetchUsers() {
        log.info("Calling JSONPlaceholder API");
        List<UserDto> userDtoList = webClient.get()
                .uri("/users")
                .retrieve()
                .bodyToFlux(UserDto.class)
                .collectList()
                .block();
        log.info("Received {} users from JSONPlaceholder", userDtoList.size());
        return userDtoList;
    }
}