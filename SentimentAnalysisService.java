package com.example.BACK.service;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class SentimentAnalysisService {

    private StanfordCoreNLP pipeline;

    public SentimentAnalysisService() {
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize, ssplit, pos, parse, sentiment");
        this.pipeline = new StanfordCoreNLP(properties);
    }

    public String analyzeSentiment(String text) {
        if (text == null || text.isEmpty()) {
            return "NEUTRAL";
        }

        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            // Log pour vérifier le sentiment analysé
            System.out.println("Sentiment: " + sentiment);
            return sentiment.toUpperCase();
        }
        return "NEUTRAL";
    }

    public static void main(String[] args) {
        SentimentAnalysisService service = new SentimentAnalysisService();
        System.out.println("Sentiment: " + service.analyzeSentiment("This project is amazing!"));
    }
}
