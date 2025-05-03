package com.example.BACK.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Immobilier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    @NotNull(message = "La latitude est obligatoire")
    private Double latitude;

    @NotNull(message = "La longitude est obligatoire")
    private Double longitude;

    @NotNull(message = "Le prix est obligatoire")
    private Double prix;

    @NotNull(message = "La superficie est obligatoire")
    private Float superficie;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le type d'immobilier est obligatoire")
    private TypeImmobilier type;

    private String photoPath;

    // Attributs manquants
    private Integer nombrePieces; // Nombre de pièces
    private Integer anneeConstruction; // Année de construction
    private Float distanceCentre; // Distance au centre-ville
    private Float distanceEcoles; // Distance aux écoles
    private String etat; // Etat du bien (par exemple: "Neuf", "Bon état", etc.)
    private String etage;
    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Float getSuperficie() {
        return superficie;
    }

    public void setSuperficie(Float superficie) {
        this.superficie = superficie;
    }

    public TypeImmobilier getType() {
        return type;
    }

    public void setType(TypeImmobilier type) {
        this.type = type;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {

    }

    public String getAdresse() {
        return "Adresse: " + this.ville + ", Lat: " + this.latitude + ", Long: " + this.longitude;
    }

    // Getters et Setters pour les nouveaux attributs

    public Integer getNombrePieces() {
        return nombrePieces;
    }

    public void setNombrePieces(Integer nombrePieces) {
        this.nombrePieces = nombrePieces;
    }

    public Integer getAnneeConstruction() {
        return anneeConstruction;
    }

    public void setAnneeConstruction(Integer anneeConstruction) {
        this.anneeConstruction = anneeConstruction;
    }

    public Float getDistanceCentre() {
        return distanceCentre;
    }

    public void setDistanceCentre(Float distanceCentre) {
        this.distanceCentre = distanceCentre;
    }

    public Float getDistanceEcoles() {
        return distanceEcoles;
    }

    public void setDistanceEcoles(Float distanceEcoles) {
        this.distanceEcoles = distanceEcoles;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public void setEtage(String etage) {
    }

    public String getEtage() {
        return etage;
    }

    public void setPlaceParking(boolean placeParking) {
    }

    public void setPrixSyndic(double prixSyndic) {
    }

    public void setAscenseur(boolean ascenseur) {
    }

    public void setAdresse(String adresse) {
    }
}
