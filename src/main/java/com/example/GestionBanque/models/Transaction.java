package com.example.GestionBanque.models;

import com.example.GestionBanque.enums.TypeTransaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private TypeTransaction typeTransaction;

    private Double montant;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateTransaction;

    private String libelle;

    @ManyToOne
    @JoinColumn(name = "compte_source_id")
    @JsonIgnore
    private Compte compteSource;
}
