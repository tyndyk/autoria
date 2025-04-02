package com.example.auto_ria.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "stripe")
public class StripeConfigProperties {
    private String successUrl;
    private String cancelUrl;
    private String priceId;
    private String key;
    private String webhookKey;
}
