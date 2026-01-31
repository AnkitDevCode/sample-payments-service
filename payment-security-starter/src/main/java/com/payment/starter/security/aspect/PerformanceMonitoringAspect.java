package com.payment.starter.security.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class PerformanceMonitoringAspect {

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    @Around("within(@org.springframework.stereotype.Service *) || " +
            "within(@org.springframework.web.bind.annotation.RestController *)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("PERFORMANCE: Method [{}] executed in {} ms", methodName, executionTime);

            if (meterRegistry != null) {
                Timer.builder("method.execution.time")
                        .tag("method", methodName)
                        .register(meterRegistry)
                        .record(executionTime, java.util.concurrent.TimeUnit.MILLISECONDS);
            }
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("PERFORMANCE: Method [{}] failed after {} ms", methodName, executionTime);
            throw throwable;
        }
    }
}