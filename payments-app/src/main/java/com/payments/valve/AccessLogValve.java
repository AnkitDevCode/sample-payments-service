package com.payments.valve;

import jakarta.servlet.ServletException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Custom Tomcat Valve for Logs access information
 */
@Slf4j
@Getter
@Setter
public class AccessLogValve extends ValveBase {

    private static final Logger ACCESS_LOG = LoggerFactory.getLogger("ACCESS_LOG");
    private boolean logAccessInfo = true;

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        try {
            getNext().invoke(request, response);
        } finally {
            if (logAccessInfo) {
                logAccess(request, response, startTime);
            }
        }
    }

    private void logAccess(Request request, Response response, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String idempotencyKey = request.getHeader("idempotencyKey");
        int status = response.getStatus();
        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        String fullUri = query != null ? uri + "?" + query : uri;

        ACCESS_LOG.info("ACCESS_LOG: {} {} | Status: {} | Duration: {}ms | IP: {}  | User-Agent: {}",
                method, fullUri, status, duration, remoteAddr,
                userAgent != null ? userAgent : "Unknown");
    }
}