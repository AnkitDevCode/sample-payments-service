package com.payments.interceptor;

import com.payments.config.ExternalLoggingProperties;
import com.payments.dto.BufferedClientHttpResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@AllArgsConstructor
public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final int MAX_BODY_LENGTH = 2 * 1024 * 1024; // 2 MB

    private final ExternalLoggingProperties loggingProperties;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        long start = System.currentTimeMillis();
        if (loggingProperties.bodyEnabled()) {
            log.info(
                    "External API request | method={} url={} headers={} body={}",
                    request.getMethod(),
                    request.getURI(),
                    request.getHeaders(),
                    truncate(body)
            );
        } else {
            log.info(
                    "External API request | method={} url={}",
                    request.getMethod(),
                    request.getURI()
            );
        }

        ClientHttpResponse response = execution.execute(request, body);

        byte[] responseBody = readAllBytes(response.getBody());

        if (loggingProperties.bodyEnabled()) {
            log.info(
                    "External API response | method={} url={} headers={} status={} latencyMs={} body={}",
                    request.getMethod(),
                    request.getURI(),
                    response.getHeaders(),
                    response.getStatusCode().value(),
                    System.currentTimeMillis() - start,
                    truncate(responseBody)
            );
        } else {
            log.info(
                    "External API response | method={} url={} status={} latencyMs={}",
                    request.getMethod(),
                    request.getURI(),
                    response.getStatusCode().value(),
                    System.currentTimeMillis() - start
            );
        }

        // IMPORTANT: wrap response so body can be read again
        return new BufferedClientHttpResponse(response, responseBody);
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        return inputStream.readAllBytes();
    }

    private String truncate(byte[] body) {
        if (body.length == 0) {
            return "Empty body";
        }
        if (body.length > MAX_BODY_LENGTH) {
            return new String(body, 0, MAX_BODY_LENGTH, StandardCharsets.UTF_8)
                    + "...(truncated, max=2MB)";
        }
        return new String(body, StandardCharsets.UTF_8);
    }
}
