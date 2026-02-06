package com.example.GestionBanque.controller;

import com.example.GestionBanque.dto.CompteClientResponse;
import com.example.GestionBanque.dto.OperationRequest;
import com.example.GestionBanque.models.Client;
import com.example.GestionBanque.models.Compte;
import com.example.GestionBanque.repository.CompteRepository;
import com.example.GestionBanque.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/guichet")
@PreAuthorize("hasRole('CAISSIER')")
@CrossOrigin(origins = "*")
public class GuichetController {

    @Autowired
    private CompteService compteService;

    @Autowired
    private CompteRepository compteRepository;

    // ===== DÉPÔT D'ESPÈCES =====
    @PostMapping("/depot")
    public ResponseEntity<?> deposerEspeces(@RequestBody OperationRequest request, Principal principal) {
        try {
            String nomCaissier = principal.getName();
            compteService.AjouterSoldeGuicher(request.getNumeroCompte(), request.getMontant(), nomCaissier);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Dépôt de " + request.getMontant() + " EUR validé par " + nomCaissier);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ===== RETRAIT D'ESPÈCES =====
    @PostMapping("/retrait")
    public ResponseEntity<?> retirerEspeces(@RequestBody OperationRequest request) {
        try {
            compteService.retirerSolde(request.getNumeroCompte(), request.getMontant());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Retrait de " + request.getMontant() + " EUR effectué avec succès");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ===== RECHERCHER UN COMPTE =====
    @GetMapping("/recherche-compte")
    public ResponseEntity<CompteClientResponse> trouverCompte(@RequestParam String query) {
        // Recherche le compte par email ou nom d'utilisateur
        Compte compte = compteRepository.findByClientUtilisateurNomUtilisateurOrClientUtilisateurEmail(query, query)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));

        // Récupère le client associé
        Client client = compte.getClient();

        // Construit la réponse avec les informations du client
        CompteClientResponse.UtilisateurDetails utilisateur = new CompteClientResponse.UtilisateurDetails(
                client.getUtilisateur().getNomUtilisateur(),
                client.getUtilisateur().getEmail()
        );

        CompteClientResponse.ClientDetails clientDetails = new CompteClientResponse.ClientDetails(
                client.getId(),
                client.getNom(),
                client.getPrenom(),
                client.getTelephone(),
                client.getAdresse(),
                utilisateur
        );

        CompteClientResponse response = new CompteClientResponse(
                compte.getId(),
                compte.getNumeroCompte(),
                compte.getSolde(),
                compte.getTypeCompte().toString(),  // Convertit l'enum en String
                compte.getActif(),
                compte.getDateCreation().toString(),
                compte.getDevise(),
                clientDetails
        );

        return ResponseEntity.ok(response);
    }
}