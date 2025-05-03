package com.example.BACK.service;

import org.springframework.stereotype.Service;

@Service
public class ArcGISService {

    private static final String API_URL = "https://services.arcgis.com/..."; // URL de l'API ArcGIS

    public Double calculateNewPrice(String adresse, Double currentPrice) {
        // Simuler une requête HTTP à l'API ArcGIS pour obtenir le taux d'augmentation
        double marketIncrease = getMarketIncrease(adresse);

        // Calcul du nouveau prix
        return currentPrice * (1 + marketIncrease);
    }

    private double getMarketIncrease(String adresse) {
        // Simuler une requête API qui retourne un taux d'augmentation entre 1% et 10%
        return Math.random() * 0.1;
    }
}
