package com.example.GestionBanque.dto;

import lombok.Data;

@Data
public class RegistrationRequest {
    // Infos pour la table Utilisateur
    private String nomUtilisateur;
    private String motDePasse;
    private String email;

    // Infos pour la table Client
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
}
