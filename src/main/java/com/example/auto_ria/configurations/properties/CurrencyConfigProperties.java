package com.example.auto_ria.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "currency")
public class CurrencyConfigProperties {
    private String redisKey;
    private String apiUrl;
    private String accessKey;
}
