package com.example.GestionBanque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfilResponse {

    // Informations du compte bancaire
    private String numeroCompte;
    private Double solde;
    private String dateCreation;  // ou LocalDateTime si tu préfères

    // Informations du client (nested object)
    private ClientInfo client;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClientInfo {
        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private String adresse;
    }
}