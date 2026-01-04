package com.payments.dto;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public record BufferedClientHttpResponse(
        ClientHttpResponse original,
        byte[] body
) implements ClientHttpResponse {

    // prevent external modification
    public BufferedClientHttpResponse(ClientHttpResponse original, byte[] body) {
        this.original = original;
        this.body = body != null ? body.clone() : new byte[0];
    }

    @Override
    public InputStream getBody() {
        return new ByteArrayInputStream(body);
    }

    @Override
    public HttpHeaders getHeaders() {
        return original.getHeaders();
    }

    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return original.getStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return original.getStatusText();
    }

    @Override
    public void close() {
        original.close();
    }
}