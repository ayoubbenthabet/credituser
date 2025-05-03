package com.example.BACK.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.BACK.model.ClientRecherche;
import com.example.BACK.model.Immobilier;
import com.example.BACK.repository.ImmobilierRepository;

import java.util.*;
import java.util.logging.Logger;

@Service
public class ImmobilierService {

    @Autowired
    private ImmobilierRepository immobilierRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ArcGISService arcGISService;

    private static final Logger LOGGER = Logger.getLogger(ImmobilierService.class.getName());

    // ✅ Recherche dynamique par n'importe quel attribut
    public List<Immobilier> rechercheDynamique(Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Immobilier> query = cb.createQuery(Immobilier.class);
        Root<Immobilier> root = query.from(Immobilier.class);

        List<Predicate> predicates = new ArrayList<>();

        filters.forEach((key, value) -> {
            if (value != null && !value.toString().isEmpty()) {
                predicates.add(cb.equal(root.get(key), value));
            }
        });

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }

    // ✅ Ajouter un bien immobilier
    public Immobilier addImmobilier(Immobilier immobilier) {
        return immobilierRepository.save(immobilier);
    }

    // ✅ Modifier un bien immobilier
    public Immobilier updateImmobilier(Long id, Immobilier immobilier) {
        return immobilierRepository.findById(id).map(existingImmobilier -> {
            existingImmobilier.setAdresse(immobilier.getAdresse());
            existingImmobilier.setPrix(immobilier.getPrix());
            existingImmobilier.setType(immobilier.getType());
            existingImmobilier.setSuperficie(immobilier.getSuperficie());
            existingImmobilier.setPhotoPath(immobilier.getPhotoPath());
            existingImmobilier.setAnneeConstruction(immobilier.getAnneeConstruction());
            existingImmobilier.setDistanceCentre(immobilier.getDistanceCentre());
            existingImmobilier.setDistanceEcoles(immobilier.getDistanceEcoles());
            existingImmobilier.setEtat(immobilier.getEtat());
            existingImmobilier.setLatitude(immobilier.getLatitude());
            existingImmobilier.setLongitude(immobilier.getLongitude());
            existingImmobilier.setNombrePieces(immobilier.getNombrePieces());
            existingImmobilier.setEtage(immobilier.getEtage());

            return immobilierRepository.save(existingImmobilier);
        }).orElseThrow(() -> new NoSuchElementException("Bien immobilier avec ID " + id + " non trouvé"));
    }


    // ✅ Supprimer un bien immobilier
    public void deleteImmobilier(Long id) {
        immobilierRepository.deleteById(id);
    }

    // ✅ Récupérer un bien immobilier par ID
    public Optional<Immobilier> findImmobilierById(Long id) {
        return immobilierRepository.findById(id);
    }

    // ✅ Récupérer tous les biens immobiliers
    public List<Immobilier> getAllImmobiliers() {
        return immobilierRepository.findAll();
    }

    // ✅ Estimation de prix via API externe (Machine Learning)
    public Double estimerPrix(Immobilier immobilier) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> request = new HashMap<>();
        request.put("superficie", immobilier.getSuperficie());
        request.put("nombre_pieces", immobilier.getNombrePieces());
        request.put("latitude", immobilier.getLatitude());
        request.put("longitude", immobilier.getLongitude());
        request.put("annee_construction", immobilier.getAnneeConstruction());
        request.put("distance_centre", immobilier.getDistanceCentre());
        request.put("distance_ecoles", immobilier.getDistanceEcoles());
        request.put("etat", immobilier.getEtat().toString());

        Map<String, Object> response = restTemplate.postForObject("http://localhost:5000/predict", request, Map.class);
        return (Double) response.get("estimation_prix");
    }

    // ✅ Recommandation de prix basée sur les critères d'un client
    public Double recommanderPrix(ClientRecherche recherche) {
        Immobilier immobilierRecherche = new Immobilier();
        immobilierRecherche.setNombrePieces(recherche.getNombrePieces());
        immobilierRecherche.setDistanceCentre(recherche.getDistanceCentre());
        immobilierRecherche.setEtage(recherche.getEtage());
        immobilierRecherche.setPlaceParking(recherche.isPlaceParking());
        immobilierRecherche.setPrixSyndic(recherche.getPrixSyndic());
        immobilierRecherche.setAscenseur(recherche.isAscenseur());

        return estimerPrix(immobilierRecherche);
    }
    public String getGoogleMapsLink(Long id) {
        Optional<Immobilier> immobilierOptional = immobilierRepository.findById(id);

        if (immobilierOptional.isPresent()) {
            Immobilier immobilier = immobilierOptional.get();
            if (immobilier.getLatitude() != null && immobilier.getLongitude() != null) {
                return "https://www.google.com/maps?q=" + immobilier.getLatitude() + "," + immobilier.getLongitude();
            }
        }
        return "Localisation non disponible";
    }





}
