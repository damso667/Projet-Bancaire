package com.example.GestionBanque.controller;

import com.example.GestionBanque.dto.MdpRequest;
import com.example.GestionBanque.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

public class UtilisateurController {
    @Autowired
    private UtilisateurService utilisateurService;

    @PatchMapping("/modifier-mdp")
    public ResponseEntity<String> updatePassword(@RequestBody MdpRequest request, Principal principal) {
        utilisateurService.changerMotDePasse(principal.getName(), request.getAncienMdp(), request.getNouveauMdp());
        return ResponseEntity.ok("Mot de passe mis à jour avec succès");
    }
}
