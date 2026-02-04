package com.example.GestionBanque.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VirementRequest {
    private String numeroCompteSource;
    private String numeroCompteDestination;
    private Double montant;
}
