package com.payments.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WebClientLoggingFilter {

    public ExchangeFilterFunction requestLogger() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info(
                    "External API request | method={} url={} headers={}",
                    request.method().name(),
                    request.url(),
                    sanitize(request.headers())
            );

            return Mono.just(request);
        });
    }

    public ExchangeFilterFunction responseLogger() {
        return (request, next) -> {
            long start = System.currentTimeMillis();
            return next.exchange(request)
                    .doOnSuccess(response ->
                            log.info(
                                    "External API response | method={} url={} status={} latencyMs={}",
                                    request.method().name(),
                                    request.url(),
                                    response.statusCode().value(),
                                    System.currentTimeMillis() - start
                            )
                    );
        };
    }

    public ExchangeFilterFunction errorLogger() {
        return (request, next) ->
                next.exchange(request)
                        .doOnError(ex ->
                                log.error(
                                        "External API call failed | method={} url={} error={}",
                                        request.method().name(),
                                        request.url(),
                                        ex.getMessage(),
                                        ex
                                )
                        );
    }

    private Map<String, String> sanitize(HttpHeaders headers) {
        return headers.headerSet().stream()
                .filter(e -> !HttpHeaders.AUTHORIZATION.equalsIgnoreCase(e.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> String.join(",", e.getValue())
                ));
    }
}
