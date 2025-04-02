package com.example.auto_ria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableKafka
@OpenAPIDefinition(info = @Info(title = "Auto Ria API", version = "1.0", description = "Car API Documentation"))

public class AutoRiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoRiaApplication.class, args);
    }

}
