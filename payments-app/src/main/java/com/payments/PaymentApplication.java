package com.payments;

import jakarta.servlet.ServletContext;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class PaymentApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(PaymentApplication.class, args);
    }

    @Bean
    public CommandLineRunner logFilters(ServletContext servletContext) {
        return args -> {
            System.out.println("=== REGISTERED FILTERS ===");
            servletContext.getFilterRegistrations().forEach((name, registration) -> {
                boolean isAsync = false;
                try {
                    // We use reflection because the getter isn't always public on the interface
                    var field = registration.getClass().getDeclaredField("asyncSupported");
                    field.setAccessible(true);
                    isAsync = (boolean) field.get(registration);
                } catch (Exception e) {
                    // Fallback: check the string representation
                    isAsync = registration.toString().contains("isAsyncSupported=true");
                }
                System.out.printf("Filter: %-30s | Async Supported: %s%n", name, isAsync);
            });
            System.out.println("==========================");
        };
    }
}
