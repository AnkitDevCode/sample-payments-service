package com.payments;

import org.springframework.boot.ApplicationRunner;
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
    ApplicationRunner runner(ConfigurableApplicationContext ctx) {
        return args -> {
            ConditionEvaluationReport report = ctx.getBean(ConditionEvaluationReport.class);
            report.getConditionAndOutcomesBySource().forEach((source, outcome) -> {
            });
        };
    }
}
