package com.example.GestionBanque.controller;

import com.example.GestionBanque.dto.RechargementRequest;
import com.example.GestionBanque.dto.VirementRequest;
import com.example.GestionBanque.models.Compte;
import com.example.GestionBanque.models.Transaction;
import com.example.GestionBanque.repository.CompteRepository;
import com.example.GestionBanque.services.CompteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final CompteService compteService;
    private final CompteRepository compteRepository;

    @PostMapping("/virement")
    public ResponseEntity<String> virement(@RequestBody VirementRequest request) {
        try {
            compteService.effectuerVirement(request);
            return ResponseEntity.ok("Virement de " + request.getMontant() + " EUR effectué avec succès !");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/recharger")
    public ResponseEntity<String> rechargerMonCompte(@RequestBody RechargementRequest request, Principal principal) {
        // 1. On récupère le nom de l'utilisateur connecté via le Token JWT
        String username = principal.getName();

        // 2. On trouve son compte en base
        Compte compte = compteRepository.findByClientUtilisateurNomUtilisateur(username)
                .orElseThrow(() -> new RuntimeException("Votre compte bancaire n'existe pas"));

        // 3. On appelle ton service avec les DEUX paramètres requis :
        // param 1 : le numéro de compte (récupéré du compte trouvé)
        // param 2 : le montant (récupéré du JSON envoyé)
        compteService.ajouterSolde(compte.getNumeroCompte(), request.getMontant());

        return ResponseEntity.ok("Votre compte a été rechargé de " + request.getMontant() + " EUR.");
    }

    @GetMapping("/mes-transactions")
    public ResponseEntity<List<Transaction>> consulterHistorique(Principal principal) {
        // Identifier l'utilisateur connecté
        String username = principal.getName();

        //  Trouver son compte
        Compte compte = compteRepository.findByClientUtilisateurNomUtilisateur(username)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));

        //  Récupérer et retourner la liste
        List<Transaction> historique = compteService.obtenirHistorique(compte.getNumeroCompte());
        return ResponseEntity.ok(historique);
    }

    @GetMapping("/mon-profil")
    public ResponseEntity<?> getMonProfil(Principal principal) {
        String username = principal.getName();
        return compteRepository.findByClientUtilisateurNomUtilisateur(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}

