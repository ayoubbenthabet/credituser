package com.example.BACK.service;

public class SentimentRequest {
    private String message;

    public SentimentRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
