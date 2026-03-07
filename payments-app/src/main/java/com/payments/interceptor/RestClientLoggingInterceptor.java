package com.payments.interceptor;

import com.payments.config.ExternalLoggingProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public record RestClientLoggingInterceptor(ExternalLoggingProperties props) implements ClientHttpRequestInterceptor {

    private static final int MAX = 2 * 1024 * 1024;

    @Override
    public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution ex) throws IOException {
        long start = System.nanoTime();

        log.info("External API request | method={} url={}", req.getMethod(), req.getURI());

        if (props.bodyEnabled() && log.isDebugEnabled()) {
            log.debug("External API request body={}", truncate(body));
        }

        ClientHttpResponse res = ex.execute(req, body);
        long latencyMs = (System.nanoTime() - start) / 1_000_000;

        log.info("External API response | method={} url={} status={} latencyMs={}",
                req.getMethod(), req.getURI(), res.getStatusCode().value(), latencyMs);

        if (props.bodyEnabled() && log.isDebugEnabled()) {
            log.debug("External API response body={}", truncate(res.getBody().readAllBytes()));
        }

        return res;
    }

    private static String truncate(byte[] b) {
        if (b == null || b.length == 0) return "<empty body>";
        int len = Math.min(b.length, MAX);
        return new String(b, 0, len, StandardCharsets.UTF_8) + (b.length > MAX ? "...(truncated)" : "");
    }
}