package com.example.auto_ria.kafka;

import java.time.Instant;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.auto_ria.dao.cars.CarViewRepository;
import com.example.auto_ria.models.car.CarView;
import com.example.auto_ria.services.validation.car.CarViewCacheService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ViewEventConsumer {

    private final CarViewRepository carViewRepository;
    private final CarViewCacheService carViewCacheService;

    @KafkaListener(topics = "car_views", groupId = "car-views-group")
    public void consume(String message) {
        try {
            String[] data = message.split(",");
            Long carId = Long.parseLong(data[0]);
            Long userId = data[1].equals("null") ? null : Long.parseLong(data[1]);
            String ipAddress = data[2];
            String userAgent = data[3];
            Instant viewedAt = Instant.ofEpochMilli(Long.parseLong(data[4]));

            CarView carView = CarView.builder()
                    .carId(carId)
                    .userId(userId)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .viewedAt(viewedAt)
                    .build();

            carViewRepository.save(carView);

            log.info("Consuming Kafka message for carId: {}", carId);
            carViewCacheService.incrementView(carId);

            log.info("Successfully processed Kafka message: {}", message);

        } catch (Exception e) {
            log.error("Failed to process Kafka message: {}", message, e);
        }
    }

    private final DataSource dataSource;

    @PreDestroy
    public void shutdown() {
        if (dataSource instanceof com.zaxxer.hikari.HikariDataSource) {
            ((com.zaxxer.hikari.HikariDataSource) dataSource).close();
        }
    }
}
