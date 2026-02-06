package com.example.GestionBanque.controller;

import com.example.GestionBanque.dto.ProfilResponse;
import com.example.GestionBanque.dto.RechargementRequest;
import com.example.GestionBanque.dto.UpdateProfilRequest;
import com.example.GestionBanque.dto.VirementRequest;
import com.example.GestionBanque.models.Client;
import com.example.GestionBanque.models.Compte;
import com.example.GestionBanque.models.Transaction;
import com.example.GestionBanque.repository.ClientRepository;
import com.example.GestionBanque.repository.CompteRepository;
import com.example.GestionBanque.services.CompteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final CompteService compteService;
    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;


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
    public ResponseEntity<ProfilResponse> getMonProfil(Principal principal) {
        //  Récupère le nom d'utilisateur depuis le token JWT
        String username = principal.getName();

        // Trouve le compte bancaire de l'utilisateur
        Compte compte = compteRepository.findByClientUtilisateurNomUtilisateur(username)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));

        //  Récupère les informations du client
        Client client = compte.getClient();

        //  Construit la réponse avec le bon format
        ProfilResponse.ClientInfo clientInfo = new ProfilResponse.ClientInfo(
                client.getNom(),
                client.getPrenom(),
                client.getUtilisateur().getEmail(),
                client.getTelephone(),
                client.getAdresse()
        );

        ProfilResponse response = new ProfilResponse(
                compte.getNumeroCompte(),
                compte.getSolde(),
                compte.getDateCreation().toString(),  // Convertit en String
                clientInfo
        );

        return ResponseEntity.ok(response);
    }
    @PutMapping("/mon-profil")
    public ResponseEntity<?> updateMonProfil(
            @RequestBody UpdateProfilRequest request,
            Principal principal) {

        try {
            // 1. Récupère le nom d'utilisateur
            String username = principal.getName();

            // 2. Trouve le compte
            Compte compte = compteRepository.findByClientUtilisateurNomUtilisateur(username)
                    .orElseThrow(() -> new RuntimeException("Compte introuvable"));

            //  Récupère le client
            Client client = compte.getClient();

            // Met à jour les informations du client
            client.setNom(request.getNom());
            client.setPrenom(request.getPrenom());
            client.setTelephone(request.getTelephone());
            client.setAdresse(request.getAdresse());

            //  Met à jour l'email dans l'utilisateur
            client.getUtilisateur().setEmail(request.getEmail());

            //  Sauvegarde les modifications
            clientRepository.save(client);

            //  Retourne une réponse de succès
            Map<String, String> response = new HashMap<>();
            response.put("message", "Profil mis à jour avec succès");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur lors de la mise à jour du profil: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}

