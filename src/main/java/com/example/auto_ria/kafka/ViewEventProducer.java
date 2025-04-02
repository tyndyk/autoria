package com.example.auto_ria.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ViewEventProducer {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "car_views";

    public void sendViewEvent(Long carId, Long userId, String ipAddress, String userAgent) {
        String event = carId + "," + (userId != null ? userId : "null") + "," + ipAddress + "," + userAgent + "," + System.currentTimeMillis();
        kafkaTemplate.send(TOPIC, event);
    }
}
