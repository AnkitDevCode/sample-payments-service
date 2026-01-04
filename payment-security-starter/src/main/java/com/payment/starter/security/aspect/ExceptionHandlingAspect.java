package com.payment.starter.security.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Slf4j
public class ExceptionHandlingAspect {

    @AfterThrowing(pointcut = "@within(org.springframework.stereotype.Service) || " +
            "@within(org.springframework.web.bind.annotation.RestController)",
            throwing = "exception")
    public void handleException(Exception exception) {
        log.error("EXCEPTION: Caught exception - Type: {}, Message: {}",
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                exception);
    }
}