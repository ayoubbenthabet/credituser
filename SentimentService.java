package com.example.BACK.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@Service
public class SentimentService {

    private final String flaskUrl = "http://localhost:5000/analyse";  // L'URL de votre service Flask

    // Méthode pour appeler Flask et obtenir le sentiment
    public String getSentimentFromFlask(String message) {
        RestTemplate restTemplate = new RestTemplate();

        // Créer les en-têtes HTTP nécessaires
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Créer le corps de la requête JSON
        String jsonBody = "{\"message\": \"" + message + "\"}";

        // Effectuer la requête HTTP POST vers Flask
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, String.class);

        // Extraire et retourner le sentiment de la réponse
        String responseBody = response.getBody();
        return responseBody.contains("sentiment") ? responseBody.split(":")[1].replace("\"", "").trim() : "neutre";
    }
}
