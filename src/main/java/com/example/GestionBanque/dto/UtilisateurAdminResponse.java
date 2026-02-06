package com.example.GestionBanque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurAdminResponse {
    private Long id;
    private String nomUtilisateur;
    private String email;
    private String role;
    private Boolean actif;

    // Informations supplémentaires si c'est un client
    private String nom;
    private String prenom;
    private String telephone;
}