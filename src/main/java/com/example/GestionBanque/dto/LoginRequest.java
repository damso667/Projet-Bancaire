package com.example.GestionBanque.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String identifier;
    private String motDePasse;
}