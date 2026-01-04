package com.payment.starter.security.filter;

import com.payment.starter.security.config.SecurityProperties;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Slf4j
public class RequestLoggingFilter implements Filter {

    private final SecurityProperties.LoggingConfig config;

    public RequestLoggingFilter(SecurityProperties.LoggingConfig config) {
        this.config = config;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ContentCachingResponseWrapper wrappedResponse = null;
        ContentCachingRequestWrapper wrappedRequest = null;
        try {
            wrappedRequest = new ContentCachingRequestWrapper(httpRequest, config.getMaxBodyLength());
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            wrappedResponse = new ContentCachingResponseWrapper(httpResponse);
            chain.doFilter(wrappedRequest, response);
            wrappedResponse.copyBodyToResponse();
        } finally {
            logRequest(wrappedRequest);
            logResponse(wrappedResponse);
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== Incoming Request ==========\n");
        logMessage.append("Method: ").append(request.getMethod()).append("\n");
        logMessage.append("URI: ").append(request.getRequestURI()).append("\n");
        logMessage.append("Query String: ").append(request.getQueryString()).append("\n");

        if (config.isLogHeaders()) {
            logMessage.append("Headers:\n");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                logMessage.append("  ").append(headerName).append(": ")
                        .append(request.getHeader(headerName)).append("\n");
            }
        }

        if (config.isLogBody()) {
            byte[] content = request.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                if (body.length() > config.getMaxBodyLength()) {
                    body = body.substring(0, config.getMaxBodyLength()) + "... (truncated)";
                }
                logMessage.append("Body: ").append(body).append("\n");
            }
        }

        logMessage.append("======================================\n");
        log.info(logMessage.toString());
    }

    private void logResponse(ContentCachingResponseWrapper response) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n========== Outgoing Response ==========\n");
        logMessage.append("Status: ").append(response.getStatus()).append("\n");

        if (config.isLogHeaders()) {
            logMessage.append("Headers:\n");
            response.getHeaderNames().forEach(headerName ->
                    logMessage.append("  ").append(headerName).append(": ")
                            .append(response.getHeader(headerName)).append("\n")
            );
        }

        if (config.isLogBody()) {
            byte[] content = response.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                if (body.length() > config.getMaxBodyLength()) {
                    body = body.substring(0, config.getMaxBodyLength()) + "... (truncated)";
                }
                logMessage.append("Body: ").append(body).append("\n");
            }
        }
        logMessage.append("=======================================\n");
        log.info(logMessage.toString());
    }
}