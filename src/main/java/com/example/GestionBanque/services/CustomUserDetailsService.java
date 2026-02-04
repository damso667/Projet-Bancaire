package com.example.GestionBanque.services;

import com.example.GestionBanque.models.Utilisateur;
import com.example.GestionBanque.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // 1. On cherche l'utilisateur dans ta base MySQL
        Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateurOrEmail(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + identifier));

        // 2. On transforme ton rôle (ex: "CLIENT") en une autorité Spring (ex: "ROLE_CLIENT")
        String roleAvecPrefixe = "ROLE_" + utilisateur.getRole();

        // 3. On retourne l'objet User standard de Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(utilisateur.getNomUtilisateur())
                .password(utilisateur.getMotDePasse())
                .authorities(roleAvecPrefixe) // Spring va créer la SimpleGrantedAuthority avec le bon nom
                .build();
    }
}
