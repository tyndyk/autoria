package com.example.auto_ria.services.otherApi;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProfanityFilterService {

    private static final Logger log = LoggerFactory.getLogger(ProfanityFilterService.class);
    private Environment environment;

    public String containsProfanity(String text) {
        try {
            JsonNode root = callProfanityFilterApi(text);
            JsonNode resultNode = root.path("result");

            if (resultNode.isMissingNode()) {
                throw new IllegalArgumentException("Invalid API response: Missing 'result' field");
            }

            return resultNode.asText();
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to connect to the profanity filter API: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to filter profanity: API connection error");
        } catch (NullPointerException e) {
            log.error("Invalid API response structure: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Failed to filter profanity: Invalid API response");
        }
    }

    public boolean containsProfanityBoolean(String filtered, String plain) {
        return !filtered.equals(plain);
    }

    private JsonNode callProfanityFilterApi(String text) throws IOException, URISyntaxException {
        String apiUrl = getRequiredProperty("profanity.filter.api") +
                URLEncoder.encode(text, StandardCharsets.UTF_8);

        URI uri = new URI(apiUrl);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        InputStream responseStream = connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(responseStream);
    }

    private String getRequiredProperty(String key) {
        String value = environment.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required property: ");
        }
        return value;
    }

}