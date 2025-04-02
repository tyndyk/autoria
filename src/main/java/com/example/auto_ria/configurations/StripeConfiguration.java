package com.example.auto_ria.configurations;

import org.springframework.context.annotation.Configuration;

import com.example.auto_ria.configurations.properties.StripeConfigProperties;
import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class StripeConfiguration {

    private final StripeConfigProperties stripeConfigProperties;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeConfigProperties.getKey();
    }
}