package com.example.GestionBanque.repository;

import com.example.GestionBanque.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {
    Optional<Utilisateur> findByNomUtilisateurOrEmail(String nomUtilisateur, String email);
   Optional<Utilisateur>findByEmail(String email);
   Optional<Utilisateur>findByNomUtilisateur(String nomUtilisateur);

    @Query("SELECT u FROM Utilisateur u WHERE " +
            "LOWER(u.nomUtilisateur) LIKE LOWER(concat('%', :q, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(concat('%', :q, '%')) OR " +
            "LOWER(u.role) LIKE LOWER(concat('%', :q, '%'))")
    List<Utilisateur> rechercheGlobale(@Param("q") String query);
}
