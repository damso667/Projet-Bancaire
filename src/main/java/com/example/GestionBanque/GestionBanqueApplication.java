package com.example.GestionBanque;

import com.example.GestionBanque.dto.RegistrationRequest;
import com.example.GestionBanque.enums.Role;
import com.example.GestionBanque.models.Client;
import com.example.GestionBanque.models.Utilisateur;
import com.example.GestionBanque.repository.ClientRepository;
import com.example.GestionBanque.repository.UtilisateurRepository;
import com.example.GestionBanque.services.CompteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@SpringBootApplication
public class GestionBanqueApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionBanqueApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(UtilisateurRepository utilisateurRepository,
                               CompteService compteService,
                               PasswordEncoder encoder,
                               ClientRepository clientRepository) {
        return args -> {

            //creation de quelques clients par defaut
            creerClientSiInexistant(
                    "johndoe", "freezeAd@gmail.com", "password123", "Doe", "John", "123 Rue Principale", "0123456789",
                    utilisateurRepository, clientRepository, compteService, encoder
            );

            creerClientSiInexistant(
                    "mariejoe", "damso@gmail.com", "password1234", "Joe", "Marie", "Yaoundé", "012345678",
                    utilisateurRepository, clientRepository, compteService, encoder
            );

            // creation de l'admin
            if (utilisateurRepository.findByEmail("admin@banque.com").isEmpty()) {
                Utilisateur admin = new Utilisateur();
                admin.setNomUtilisateur("admin_boss");
                admin.setEmail("admin@banque.com");
                admin.setMotDePasse(encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setActif(true);
                utilisateurRepository.save(admin);
                System.out.println("Admin créé");
            }

            // ============ 3. CRÉATION DU CAISSIER (Utilisateur uniquement) ============
            if (utilisateurRepository.findByEmail("caissier@banque.com").isEmpty()) {
                Utilisateur caissier = new Utilisateur();
                caissier.setNomUtilisateur("caissier_expert");
                caissier.setEmail("caissier@banque.com");
                caissier.setMotDePasse(encoder.encode("caissier123"));
                caissier.setRole(Role.CAISSIER);
                caissier.setActif(true);
                utilisateurRepository.save(caissier);
                System.out.println("Caissier créé");
            }
        };
    }

    // Petite méthode utilitaire pour éviter de répéter le code des clients
    private void creerClientSiInexistant(String username, String email, String pass, String nom, String prenom,
                                         String adresse, String tel, UtilisateurRepository uRepo,
                                         ClientRepository cRepo, CompteService cService, PasswordEncoder enc) {
        if (uRepo.findByEmail(email).isEmpty()) {
            Utilisateur user = new Utilisateur();
            user.setNomUtilisateur(username);
            user.setEmail(email);
            user.setMotDePasse(enc.encode(pass));
            user.setRole(Role.CLIENT);
            user.setActif(true);
            Utilisateur savedUser = uRepo.save(user);

            Client client = new Client();
            client.setNom(nom);
            client.setPrenom(prenom);
            client.setAdresse(adresse);
            client.setTelephone(tel);
            client.setDate_inscription(LocalDateTime.now());
            client.setUtilisateur(savedUser);
            Client savedClient = cRepo.save(client);

            cService.CompteClient(savedClient); // Crée le compte bancaire
            System.out.println("Client créé : " + username);
        }
    }
}