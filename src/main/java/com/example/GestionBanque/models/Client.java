package com.example.GestionBanque.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    @OneToOne
    @JoinColumn(name = "utilisateur_id") // Liaison vers le compte de connexion
    @JsonBackReference
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "client") // Un client peut avoir plusieurs comptes
    @JsonManagedReference
    private List<Compte> comptes;

    private  String nom;
    private String prenom;
    private String telephone;
    private String adresse;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime date_inscription;




}
