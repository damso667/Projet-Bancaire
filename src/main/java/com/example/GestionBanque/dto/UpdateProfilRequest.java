package com.example.GestionBanque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfilRequest {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
}