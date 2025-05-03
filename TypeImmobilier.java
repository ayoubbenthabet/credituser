package com.example.BACK.model;

public enum TypeImmobilier {
    MAISON,
    APPARTEMENT,
    VILLA,
    TERRAIN,
    LOCAL_COMMERCIAL;

    // Optionnel : Vous pouvez ajouter une méthode pour formater les noms de manière lisible
    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}