package com.example.GestionBanque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompteAdminResponse {
    private Long id;
    private String numeroCompte;
    private Double solde;
    private String typeCompte;
    private Boolean actif;
    private String dateCreation;
    private String devise;

    // Informations du client
    private String clientNom;
    private String clientPrenom;
    private String clientEmail;
    private String clientTelephone;
}