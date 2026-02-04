package com.example.GestionBanque.controller;

import com.example.GestionBanque.dto.OperationRequest;
import com.example.GestionBanque.models.Compte;
import com.example.GestionBanque.repository.CompteRepository;
import com.example.GestionBanque.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/guichet")
@PreAuthorize("hasRole('CAISSIER')")
public class GuichetController {

    @Autowired
    private CompteService compteService;

    @Autowired
    private CompteRepository compteRepository;

    //  Dépôt d'espèce (Le client donne du cash, le caissier crédite le compte)
    @PostMapping("/depot")
    public ResponseEntity<String> deposerEspeces(@RequestBody OperationRequest request, Principal principal) {
        String nomCaissier = principal.getName(); // On récupère qui est le caissier
        compteService.AjouterSoldeGuicher(request.getNumeroCompte(), request.getMontant(), nomCaissier);
        return ResponseEntity.ok("Dépôt validé par " + nomCaissier);
    }

    //  Retrai d'espèces (Le client demande du cash, le caissier débite le compte)
    @PostMapping("/retrait")
    public ResponseEntity<String> retirerEspeces(@RequestBody OperationRequest request) {
        compteService.retirerSolde(request.getNumeroCompte(), request.getMontant());
        return ResponseEntity.ok("Retrait effectué avec succès.");
    }

    @GetMapping("/recherche-compte")
    public ResponseEntity<Compte> trouverCompte(@RequestParam String query) {
        // On cherche par email ou nom d'utilisateur du client
        return compteRepository.findByClientUtilisateurNomUtilisateurOrClientUtilisateurEmail(query, query)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}
