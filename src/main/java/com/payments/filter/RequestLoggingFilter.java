package com.payments.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Instant startTime = Instant.now();
        // Log basic request info
        log.info("Incoming Request: {} {} [{}]", request.getMethod(), request.getRequestURI(), Thread.currentThread());
        filterChain.doFilter(request, response);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        log.info("Completed: {} {} ({} ms) [{}]", request.getMethod(), request.getRequestURI(), duration.toMillis(), Thread.currentThread());
    }
}