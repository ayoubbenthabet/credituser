package com.example.BACK.service;


import com.example.BACK.model.Credit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/*
@Service
public class PredictionService {

    @Value("${ml.model.url}")
    private String mlModelUrl;

    private RestTemplate restTemplate = new RestTemplate();

    public String predictCreditStatus(Credit credit) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format(
                "{"
                        + "\"gender\": \"%s\", "
                        + "\"married\": \"%s\", "
                        + "\"education\": \"%s\", "
                        + "\"selfEmployed\": \"%s\", "
                        + "\"income\": %f, "
                        + "\"loanAmount\": %f, "
                        + "\"loanTerm\": %d, ",

                credit.getGender().toString(),
                credit.getMarried().toString(),
                credit.getEducation().toString(),
                credit.getSelfEmployed().toString(),
                credit.getIncome(),
                credit.getLoanAmount(),
                credit.getLoanTerm()

        );

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(mlModelUrl + "/predict", request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            // Parse the response to get the credit status
            return responseBody.replace("{\"creditStatus\":\"", "").replace("\"}", "");
        } else {
            throw new RuntimeException("Failed to get prediction from ML model: " + response.getStatusCode());
        }
    }
}
*/
@Service
public class PredictionService {

    @Value("${ml.model.url:http://localhost:5000/predict}")
    private String mlModelUrl;

    private  RestTemplate restTemplate = new RestTemplate();

    public String predictCreditStatus(  Credit credit) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare JSON request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("gender", credit.getGender().toString());
        requestBody.put("married", credit.getMarried().toString());
        requestBody.put("education", credit.getEducation().toString());
        requestBody.put("selfEmployed", credit.getSelfEmployed().toString());
        requestBody.put("income", credit.getIncome());
        requestBody.put("loanAmount", credit.getLoanAmount());
        requestBody.put("loanTerm", credit.getLoanTerm());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(mlModelUrl, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, String> responseBody = response.getBody();
            return responseBody.get("creditStatus");
        } else {
            throw new RuntimeException("Failed to get prediction from ML model: " + response.getStatusCode());
        }
    }
}
