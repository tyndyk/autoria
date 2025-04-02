package com.example.auto_ria.services.otherApi;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.auto_ria.exceptions.general.InternalServerException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CitiesService {

    @Value("${geo.api.key}")
    private String API_KEY;

    @Value("${geo.api.host}")
    private String API_HOST;

    @Value("${geo.api.base-url}")
    private String BASE_URL;

    public void isCityInCountry(String city, String countryCode) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String url = BASE_URL + "?namePrefix=" + encodedCity;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-rapidapi-key", API_KEY)
                    .header("x-rapidapi-host", API_HOST)
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode cities = root.path("data");

            if (!cities.isArray() || cities.isEmpty()) {
                throw new IllegalArgumentException("City '" + city + "' was not found.");
            }

            for (JsonNode cityNode : cities) {
                String foundCity = cityNode.path("name").asText();
                String foundCountry = cityNode.path("countryCode").asText();

                if (foundCity.equalsIgnoreCase(city) && foundCountry.equalsIgnoreCase(countryCode)) {
                    return;
                }
            }

            throw new IllegalArgumentException("City '" + city + "' is NOT in country '" + countryCode + "'.");
        } catch (IOException | InterruptedException e) {
            throw new InternalServerException("Failed to validate city due to an API error.");
        }
    }
}
