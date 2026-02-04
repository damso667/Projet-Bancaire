package com.example.GestionBanque.services;

import com.example.GestionBanque.models.Utilisateur;
import com.example.GestionBanque.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public List<Utilisateur> listerToutLeMonde() {
        return utilisateurRepository.findAll();
    }
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void changerMotDePasse(String username, String ancienMdp, String nouveauMdp) {
        Utilisateur user = utilisateurRepository.findByNomUtilisateur(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'ancien mot de passe est correct
        if (!passwordEncoder.matches(ancienMdp, user.getMotDePasse())) {
            throw new RuntimeException("L'ancien mot de passe est incorrect");
        }

        user.setMotDePasse(passwordEncoder.encode(nouveauMdp));
        utilisateurRepository.save(user);
    }
}
