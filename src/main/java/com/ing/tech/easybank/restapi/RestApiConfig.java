package com.ing.tech.EasyBank.restapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
public class RestApiConfig {
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @Bean
    public Map<String, String> mapCurrencies(RestTemplate restTemplate){
        String url = "https://api.exchangerate.host/latest";
        ResponseEntity<String> response = restTemplate.getForEntity(
                url,
                String.class
        );
        try {
            JsonNode parent = new ObjectMapper().readTree(response.getBody());
            JsonNode chosenNode = parent.path("rates");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> currencies = mapper.convertValue(chosenNode, new TypeReference<Map<String, String>>() {
            });
            return currencies;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
