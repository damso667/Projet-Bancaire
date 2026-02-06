package com.example.GestionBanque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompteClientResponse {

    private Long id;
    private String numeroCompte;
    private Double solde;
    private String typeCompte;
    private Boolean actif;
    private String dateCreation;
    private String devise;

    // Informations du client (nested object)
    private ClientDetails client;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClientDetails {
        private Long id;
        private String nom;
        private String prenom;
        private String telephone;
        private String adresse;

        // Informations de l'utilisateur (nested)
        private UtilisateurDetails utilisateur;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UtilisateurDetails {
        private String nomUtilisateur;
        private String email;
    }
}