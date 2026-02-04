package com.example.GestionBanque.dto;

import lombok.Data;

@Data
public class OperationRequest {
    private String numeroCompte;
    private double montant;
}
