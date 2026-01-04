package com.payments.hateos;

import com.payments.model.Link;
import com.payments.model.Payment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentHateosBuilder {

    public Payment addLinks(Payment payment) {
        if (payment == null || payment.getPaymentId() == null) {
            return payment;
        }
        Map<String, Link> links = new HashMap<>();
        // This will automatically include context-path
        String selfHref = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/payments/{id}")
                .buildAndExpand(payment.getPaymentId())
                .toUriString();

        Link selfLink = new Link();
        selfLink.setHref(selfHref);
        selfLink.setRel("self");
        links.put("self", selfLink);
        payment.setLinks(links);
        return payment;
    }

    public URI buildLocationUri(String paymentId) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/payments/{id}")
                .buildAndExpand(paymentId)
                .toUri();
    }
}