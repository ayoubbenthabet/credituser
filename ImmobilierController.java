package com.example.BACK.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import  com.example.BACK.model.ClientRecherche;
import  com.example.BACK.model.Immobilier;
import  com.example.BACK.model.TypeImmobilier;
import com.example.BACK.service.ContractService;
import com.example.BACK.service.ImmobilierService;
import com.example.BACK.service.ContractService;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/immobilier")
public class ImmobilierController {

    @Autowired
    private ImmobilierService immobilierService;

    private final ContractService contractService;

    @Autowired
    public ImmobilierController(ContractService contractService) {
        this.contractService = contractService;
    }

    // ✅ Ajouter un bien immobilier
    @PostMapping("/add")
    public ResponseEntity<Immobilier> addImmobilier(@RequestBody Immobilier immobilier) {
        Immobilier addedImmobilier = immobilierService.addImmobilier(immobilier);
        return new ResponseEntity<>(addedImmobilier, HttpStatus.CREATED);
    }

    // ✅ Modifier un bien immobilier
    @PutMapping("/update/{id}")
    public ResponseEntity<Immobilier> updateImmobilier(@PathVariable Long id, @RequestBody Immobilier immobilier) {
        try {
            Immobilier updatedImmobilier = immobilierService.updateImmobilier(id, immobilier);
            return ResponseEntity.ok(updatedImmobilier);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Supprimer un bien immobilier
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteImmobilier(@PathVariable Long id) {
        immobilierService.deleteImmobilier(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }

    // ✅ Récupérer tous les biens immobiliers
    @GetMapping("/all")
    public ResponseEntity<List<Immobilier>> getAllImmobiliers() {
        List<Immobilier> immobiliers = immobilierService.getAllImmobiliers();
        return ResponseEntity.ok(immobiliers);
    }

    // ✅ Récupérer un bien immobilier par ID
    @GetMapping("/get/{id}")
    public ResponseEntity<Immobilier> getImmobilierById(@PathVariable Long id) {
        Optional<Immobilier> immobilier = immobilierService.findImmobilierById(id);
        return immobilier.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Récupérer les types d'immobilier
    @GetMapping("/types")
    public ResponseEntity<TypeImmobilier[]> getTypes() {
        return ResponseEntity.ok(TypeImmobilier.values());
    }

    // ✅ Estimer le prix d'un bien immobilier
    @PostMapping("/estimate-price")
    public ResponseEntity<Double> estimatePrice(@RequestBody Immobilier immobilier) {
        try {
            Double estimatedPrice = immobilierService.estimerPrix(immobilier);
            return ResponseEntity.ok(estimatedPrice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ Recommander un prix basé sur une recherche client
    @PostMapping("/recommander-prix")
    public ResponseEntity<Double> recommanderPrix(@RequestBody ClientRecherche recherche) {
        Double prixRecommande = immobilierService.recommanderPrix(recherche);
        return ResponseEntity.ok(prixRecommande);
    }

    // ✅ Recherche dynamique intelligente 🔎
    @PostMapping("/search")
    public ResponseEntity<List<Immobilier>> rechercheDynamique(@RequestBody Map<String, Object> filters) {
        List<Immobilier> result = immobilierService.rechercheDynamique(filters);
        return ResponseEntity.ok(result);
    }

    // ✅ Récupérer un lien Google Maps pour un bien immobilier
    @GetMapping("/maps/{id}")
    public ResponseEntity<String> getMapsLink(@PathVariable Long id) {
        String mapsLink = immobilierService.getGoogleMapsLink(id);
        return ResponseEntity.ok(mapsLink);
    }

    // ✅ Générer et télécharger un contrat d'achat en PDF 📄


    @PostMapping(value = "/contrat-vide", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> generateEmptyContract(@RequestBody Map<String, Object> requestData) {
        try {
            // Générer un contrat vide en PDF
            byte[] pdfContent = contractService.generateEmptyPdf();

            if (pdfContent == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=contrat_vide.pdf");
            headers.add("Content-Type", "application/pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}

