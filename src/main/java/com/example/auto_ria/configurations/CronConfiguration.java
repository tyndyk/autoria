package com.example.auto_ria.configurations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import com.example.auto_ria.configurations.properties.CurrencyConfigProperties;
import com.example.auto_ria.dao.auth.UserAuthDaoSQL;
import com.example.auto_ria.dao.users.UserDaoSQL;
import com.example.auto_ria.enums.ECurrency;
import com.example.auto_ria.exceptions.general.DatabaseOperationException;
import com.example.auto_ria.services.auth.AuthKeyService;

import lombok.AllArgsConstructor;

@Configuration
@EnableScheduling
@Async
@AllArgsConstructor
public class CronConfiguration {

    private final UserDaoSQL userDaoSQL;
    private final UserAuthDaoSQL userAuthDaoSQL;
    private final AuthKeyService authKeyService;
    private final CurrencyConfigProperties currencyConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStart() {
        processScheduledTasks();
    }

    private void processScheduledTasks() {
        deleteUnactivatedAccounts();
        deleteExpiredTokens();
    }

    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    @Scheduled(cron = "0 0 */12 * * ?")
    public void fetchAndCacheCurrencyRates() {
        String symbols = String.join(",", ECurrency.getCurrencySymbols());
        String url = currencyConfig.getApiUrl() + "?access_key=" + currencyConfig.getAccessKey() + "&base=EUR&symbols="
                + symbols;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            redisTemplate.opsForValue().set(currencyConfig.getRedisKey(), response.getBody());
        }
    }

    @Scheduled(cron = "0 0 0 */2 * *")
    public void deleteUnactivatedAccounts() {
        try {
            LocalDateTime cutoffDateTime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT);
            userDaoSQL.deleteAllByIsActivatedFalseAndCreatedAtBefore(cutoffDateTime);
            authKeyService.deleteUnusedKeys(
                    LocalDate.of(cutoffDateTime.getYear(), cutoffDateTime.getMonth(), cutoffDateTime.getDayOfMonth()));
        } catch (Exception e) {
            throw new DatabaseOperationException(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredTokens() {
        try {
            LocalDateTime cutoffDateTime = LocalDateTime.now().minusDays(1);
            userAuthDaoSQL.deleteAllByCreatedAtBefore(cutoffDateTime);
        } catch (Exception e) {
            throw new DatabaseOperationException(e.getMessage());
        }
    }
}
