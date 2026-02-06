package com.example.GestionBanque.models;

import com.example.GestionBanque.enums.TypeCompte;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String numeroCompte;
    private double solde;

    @Enumerated(EnumType.STRING)
    private TypeCompte typeCompte;

    @Column(name = "actif", columnDefinition = "VARCHAR(5)")
    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    public  Boolean actif = true;

    private LocalDateTime dateCreation;

    private String devise;

    @ManyToOne
    @JoinColumn(name = "client_id") // Le propriétaire du compte
    @JsonBackReference
    private Client client;

    @OneToMany(mappedBy = "compteSource") // Permet de lier les transactions source
    private List<Transaction> transactions;

}
