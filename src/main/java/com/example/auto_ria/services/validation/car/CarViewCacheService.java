package com.example.auto_ria.services.validation.car;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.auto_ria.configurations.properties.CarViewConfigProperties;
import com.example.auto_ria.models.responses.statistics.StatisticsResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CarViewCacheService {

    private final StringRedisTemplate redisTemplate;
    private CarViewConfigProperties carViewConfigProperties;

    public void incrementView(Long carId) {
        String keyAll = carViewConfigProperties.getKeyPrefix() + carId + ":all";
        String keyDay = carViewConfigProperties.getKeyPrefix() + carId + ":day";
        String keyWeek = carViewConfigProperties.getKeyPrefix() + carId + ":week";
        String keyMonth = carViewConfigProperties.getKeyPrefix() + carId + ":month";

        redisTemplate.opsForValue().increment(keyAll);

        redisTemplate.opsForValue().increment(keyDay);
        redisTemplate.expire(keyDay, Duration.ofDays(1));

        redisTemplate.opsForValue().increment(keyWeek);
        redisTemplate.expire(keyWeek, Duration.ofDays(7));

        redisTemplate.opsForValue().increment(keyMonth);
        redisTemplate.expire(keyMonth, Duration.ofDays(30));
    }

    public int getViewCount(Long carId, String period) {
        String key = carViewConfigProperties.getKeyPrefix() + carId + ":" + period;
        String count = redisTemplate.opsForValue().get(key);
        return count != null ? Integer.parseInt(count) : 0;
    }

    public StatisticsResponse getViewsCount(Long carId) {
        return StatisticsResponse.builder()
                .viewsAll(getViewCount(carId, "all"))
                .viewsDay(getViewCount(carId, "day"))
                .viewsWeek(getViewCount(carId, "week"))
                .viewsMonth(getViewCount(carId, "month"))
                .build();
    }
}
