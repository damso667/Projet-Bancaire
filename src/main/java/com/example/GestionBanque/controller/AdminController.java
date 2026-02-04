package com.example.GestionBanque.controller;

import com.example.GestionBanque.models.Compte;
import com.example.GestionBanque.models.Utilisateur;
import com.example.GestionBanque.repository.UtilisateurRepository;
import com.example.GestionBanque.services.CompteService;
import com.example.GestionBanque.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private CompteService compteService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private UtilisateurService utilisateurService;

    //  Voir tous les comptes de la banque
    @GetMapping("/comptes")
    public ResponseEntity<List<Compte>> voirTousLesComptes() {
        return ResponseEntity.ok(compteService.listerTousLesComptes());
    }

    //  Bloquer ou Débloquer un compte
    @PatchMapping("/comptes/{numeroCompte}/statut")
    public ResponseEntity<String> changerStatut(@PathVariable String numeroCompte, @RequestParam boolean actif) {
        compteService.modifierStatutCompte(numeroCompte, actif);
        String message = actif ? "Compte réactivé" : "Compte bloqué avec succès";
        return ResponseEntity.ok(message);
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Utilisateur>> voirTousLesUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.listerToutLeMonde();
        return ResponseEntity.ok(utilisateurs);
    }

    @GetMapping("/utilisateurs/recherche")
    public ResponseEntity<Utilisateur> chercherUtilisateur(@RequestParam String query) {
        return utilisateurRepository.findByNomUtilisateurOrEmail(query, query)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/utilisateurs/search")
    public ResponseEntity<List<Utilisateur>> searchAll(@RequestParam String q) {
        return ResponseEntity.ok(utilisateurRepository.rechercheGlobale(q));
    }
}
