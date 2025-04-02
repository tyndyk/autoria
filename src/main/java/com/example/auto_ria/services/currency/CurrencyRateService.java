package com.example.auto_ria.services.currency;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.auto_ria.enums.ECurrency;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class CurrencyRateService {

    private final StringRedisTemplate redisTemplate;
    private static final String REDIS_KEY = "currency:rates";

    public String getCachedRates() {
        return redisTemplate.opsForValue().get(REDIS_KEY);
    }

    public void setCarPrices(int carId, double sellerPrice, ECurrency sellerCurrency) {
        String ratesJson = redisTemplate.opsForValue().get(REDIS_KEY);
        if (ratesJson == null) {
            log.error("Currency rates not found in Redis. Fetch them first.");
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode ratesNode = objectMapper.readTree(ratesJson).path("rates");

            double sellerToEurRate = ratesNode.path(sellerCurrency.toString()).asDouble();
            if (sellerToEurRate == 0) {
                log.error("Exchange rate for {} to EUR not found.", sellerCurrency);
                return;
            }

            double basePriceEUR = sellerPrice / sellerToEurRate;

            Map<String, String> carPrices = new HashMap<>();
            for (ECurrency currency : ECurrency.values()) {
                double rate = ratesNode.path(currency.toString()).asDouble();
                double convertedPrice = basePriceEUR * rate;
                carPrices.put(currency.toString(), String.valueOf(convertedPrice));
            }

            String carKey = "car:prices:" + carId;
            redisTemplate.opsForHash().putAll(carKey, carPrices);
            log.info("Updated car {} prices in Redis", carId);
        } catch (Exception e) {
            log.error("Error parsing currency rates from Redis", e);
        }
    }

    public double getCarPriceInCurrency(int carId, ECurrency currency) {
        String carKey = "car:prices:" + carId;
        Map<Object, Object> carPrices = redisTemplate.opsForHash().entries(carKey);

        if (carPrices == null || carPrices.isEmpty()) {
            log.error("Car prices for car {} not found in Redis.", carId);
            return 0;
        }

        String price = (String) carPrices.get(currency.toString());
        if (price == null) {
            log.error("Price for currency {} not found for car {}.", currency, carId);
            return 0;
        }

        return Double.parseDouble(price);
    }

    public int convertFromEUR(int price, ECurrency toCurrency) {
        try {
            String ratesJson = redisTemplate.opsForValue().get(REDIS_KEY);
            if (ratesJson == null) {
                log.error("Currency rates not found in Redis. Fetch them first.");
                return 0;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode ratesNode = objectMapper.readTree(ratesJson).path("rates");

            double toCurrencyRate = ratesNode.path(toCurrency.toString()).asDouble();
            if (toCurrencyRate == 0) {
                log.error("Invalid exchange rate for currency: {}", toCurrency);
                return 0;
            }

            double convertedPrice = price * toCurrencyRate;
            return (int) Math.round(convertedPrice);

        } catch (JsonProcessingException e) {
            log.error("Error parsing currency rates", e);
            return 0;
        }
    }

    public int convertToEUR(int price, ECurrency fromCurrency) {
        try {
            String ratesJson = redisTemplate.opsForValue().get(REDIS_KEY);
            if (ratesJson == null) {
                log.error("Currency rates not found in Redis. Fetch them first.");
                return 0;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode ratesNode = objectMapper.readTree(ratesJson).path("rates");

            double fromCurrencyRate = ratesNode.path(fromCurrency.toString()).asDouble();
            if (fromCurrencyRate == 0) {
                log.error("Invalid exchange rate for currency: {}", fromCurrency);
                return 0;
            }

            double convertedPrice = price / fromCurrencyRate;
            return (int) Math.round(convertedPrice);

        } catch (JsonProcessingException e) {
            log.error("Error parsing currency rates", e);
            return 0;
        }
    }

}
