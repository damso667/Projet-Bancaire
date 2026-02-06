package com.example.GestionBanque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreerCaissierRequest {
    private String nomUtilisateur;
    private String email;
    private String motDePasse;
}