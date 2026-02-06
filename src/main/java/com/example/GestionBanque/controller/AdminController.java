package com.example.GestionBanque.controller;

import com.example.GestionBanque.dto.CompteAdminResponse;
import com.example.GestionBanque.dto.CreerCaissierRequest;
import com.example.GestionBanque.dto.UtilisateurAdminResponse;
import com.example.GestionBanque.enums.Role;
import com.example.GestionBanque.models.Client;
import com.example.GestionBanque.models.Compte;
import com.example.GestionBanque.models.Utilisateur;
import com.example.GestionBanque.repository.UtilisateurRepository;
import com.example.GestionBanque.services.CompteService;
import com.example.GestionBanque.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private CompteService compteService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private UtilisateurService utilisateurService;

    // ===== VOIR TOUS LES COMPTES =====
    @GetMapping("/comptes")
    public ResponseEntity<List<CompteAdminResponse>> voirTousLesComptes() {
        List<Compte> comptes = compteService.listerTousLesComptes();

        // Convertit en DTO
        List<CompteAdminResponse> response = comptes.stream()
                .map(compte -> {
                    Client client = compte.getClient();
                    return new CompteAdminResponse(
                            compte.getId(),
                            compte.getNumeroCompte(),
                            compte.getSolde(),
                            compte.getTypeCompte().toString(),
                            compte.getActif(),
                            compte.getDateCreation().toString(),
                            compte.getDevise(),
                            client != null ? client.getNom() : "N/A",
                            client != null ? client.getPrenom() : "N/A",
                            client != null ? client.getUtilisateur().getEmail() : "N/A",
                            client != null ? client.getTelephone() : "N/A"
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ===== BLOQUER/DÉBLOQUER UN COMPTE =====
    @PatchMapping("/comptes/{numeroCompte}/statut")
    public ResponseEntity<Map<String, String>> changerStatut(
            @PathVariable String numeroCompte,
            @RequestParam boolean actif) {

        compteService.modifierStatutCompte(numeroCompte, actif);

        Map<String, String> response = new HashMap<>();
        response.put("message", actif ? "Compte réactivé avec succès" : "Compte bloqué avec succès");

        return ResponseEntity.ok(response);
    }

    // ===== VOIR TOUS LES UTILISATEURS =====
    @GetMapping("/utilisateurs")
    public ResponseEntity<List<UtilisateurAdminResponse>> voirTousLesUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.listerToutLeMonde();

        // Convertit en DTO
        List<UtilisateurAdminResponse> response = utilisateurs.stream()
                .map(user -> {
                    // Vérifie si c'est un client pour avoir plus d'infos
                    Client client = user.getClient();

                    return new UtilisateurAdminResponse(
                            user.getId(),
                            user.getNomUtilisateur(),
                            user.getEmail(),
                            user.getRole().toString(),
                            user.getActif(),
                            client != null ? client.getNom() : null,
                            client != null ? client.getPrenom() : null,
                            client != null ? client.getTelephone() : null
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ===== RECHERCHER UN UTILISATEUR =====
    @GetMapping("/utilisateurs/recherche")
    public ResponseEntity<UtilisateurAdminResponse> chercherUtilisateur(@RequestParam String query) {
        Utilisateur user = utilisateurRepository.findByNomUtilisateurOrEmail(query, query)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Client client = user.getClient();

        UtilisateurAdminResponse response = new UtilisateurAdminResponse(
                user.getId(),
                user.getNomUtilisateur(),
                user.getEmail(),
                user.getRole().toString(),
                user.getActif(),
                client != null ? client.getNom() : null,
                client != null ? client.getPrenom() : null,
                client != null ? client.getTelephone() : null
        );

        return ResponseEntity.ok(response);
    }

    // ===== RECHERCHE GLOBALE =====
    @GetMapping("/utilisateurs/search")
    public ResponseEntity<List<UtilisateurAdminResponse>> searchAll(@RequestParam String q) {
        List<Utilisateur> utilisateurs = utilisateurRepository.rechercheGlobale(q);

        List<UtilisateurAdminResponse> response = utilisateurs.stream()
                .map(user -> {
                    Client client = user.getClient();
                    return new UtilisateurAdminResponse(
                            user.getId(),
                            user.getNomUtilisateur(),
                            user.getEmail(),
                            user.getRole().toString(),
                            user.getActif(),
                            client != null ? client.getNom() : null,
                            client != null ? client.getPrenom() : null,
                            client != null ? client.getTelephone() : null
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ===== CRÉER UN CAISSIER =====
    @PostMapping("/utilisateurs/caissier")
    public ResponseEntity<Map<String, String>> creerCaissier(@RequestBody CreerCaissierRequest request) {

        // Crée l'objet Utilisateur
        Utilisateur caissier = new Utilisateur();
        caissier.setNomUtilisateur(request.getNomUtilisateur());
        caissier.setEmail(request.getEmail());
        caissier.setMotDePasse(request.getMotDePasse()); // Sera encodé dans le service

        utilisateurService.creerPersonnel(caissier, "CAISSIER");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Le caissier " + request.getNomUtilisateur() + " a été créé avec succès");

        return ResponseEntity.ok(response);
    }
}