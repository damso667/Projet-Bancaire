package com.example.GestionBanque.repository;

import com.example.GestionBanque.models.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {
    Optional<Compte> findByNumeroCompte(String numeroCompte);
    Optional<Compte>findByClientUtilisateurNomUtilisateur(String userName);
    Optional<Compte> findByClientUtilisateurNomUtilisateurOrClientUtilisateurEmail(String nom, String email);
}
