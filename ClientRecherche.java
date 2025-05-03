package com.example.BACK.model;

public class ClientRecherche {
    private double superficie;
    private int nombrePieces;
    private double distanceCentre;
    private int etage;
    private boolean placeParking;
    private double prixSyndic;
    private boolean ascenseur;

    // Constructeur par défaut
    public ClientRecherche() {}

    // Getters et Setters
    public float getSuperficie() {
       return (float) superficie;
    }

    public void setSuperficie(double superficie) {
        this.superficie = superficie;
    }

    public int getNombrePieces() {
        return nombrePieces;
    }

    public void setNombrePieces(int nombrePieces) {
        this.nombrePieces = nombrePieces;
    }

    public float getDistanceCentre() {
        return (float) distanceCentre;
    }

    public void setDistanceCentre(double distanceCentre) {
        this.distanceCentre = distanceCentre;
    }

    public String getEtage() {
        return String.valueOf(etage);
    }

    public void setEtage(int etage) {
        this.etage = etage;
    }

    public boolean isPlaceParking() {
        return placeParking;
    }

    public void setPlaceParking(boolean placeParking) {
        this.placeParking = placeParking;
    }

    public double getPrixSyndic() {
        return prixSyndic;
    }

    public void setPrixSyndic(double prixSyndic) {
        this.prixSyndic = prixSyndic;
    }

    public boolean isAscenseur() {
        return ascenseur;
    }

    public void setAscenseur(boolean ascenseur) {
        this.ascenseur = ascenseur;
    }
}
