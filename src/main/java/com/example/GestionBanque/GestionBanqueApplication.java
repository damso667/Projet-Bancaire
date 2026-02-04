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

            // ============ UTILISATEUR 1 ============
            RegistrationRequest request1 = new RegistrationRequest();
            request1.setAdresse("123 Rue Principale");
            request1.setEmail("freezeAd@gmail.com");
            request1.setNom("Doe");
            request1.setPrenom("John");
            request1.setNomUtilisateur("johndoe");
            request1.setMotDePasse("password123");
            request1.setTelephone("0123456789");

            // Vérifier si l'utilisateur existe déjà
            Optional<Utilisateur> utilisateurOptional1 = utilisateurRepository.findByEmail(request1.getEmail());
            if (utilisateurOptional1.isPresent()) {
                System.out.println("✅ Utilisateur 1 déjà existant : " + utilisateurOptional1.get().getNomUtilisateur());
            } else {
                Utilisateur user1 = new Utilisateur();
                user1.setNomUtilisateur(request1.getNomUtilisateur());
                user1.setMotDePasse(encoder.encode(request1.getMotDePasse()));
                user1.setEmail(request1.getEmail());
                user1.setActif(true);
                user1.setRole(Role.CLIENT);
                Utilisateur savedUser1 = utilisateurRepository.save(user1);

                Client client1 = new Client();
                client1.setNom(request1.getNom());
                client1.setPrenom(request1.getPrenom());
                client1.setDate_inscription(LocalDateTime.now());
                client1.setAdresse(request1.getAdresse());
                client1.setTelephone(request1.getTelephone());
                client1.setUtilisateur(savedUser1);
                Client savedClient1 = clientRepository.save(client1);

                compteService.CompteClient(savedClient1);
                System.out.println("✅ Utilisateur 1 créé : " + savedUser1.getNomUtilisateur());
            }

            // ============ UTILISATEUR 2 ============
            RegistrationRequest request2 = new RegistrationRequest();
            request2.setAdresse("yaounde");
            request2.setEmail("damso@gmail.com");
            request2.setNom("joe");
            request2.setPrenom("marie");
            request2.setNomUtilisateur("mariejoe");
            request2.setMotDePasse("password1234");
            request2.setTelephone("012345678");

            // Vérifier si l'utilisateur existe déjà
            Optional<Utilisateur> utilisateurOptional2 = utilisateurRepository.findByEmail(request2.getEmail());
            if (utilisateurOptional2.isPresent()) {
                System.out.println("✅ Utilisateur 2 déjà existant : " + utilisateurOptional2.get().getNomUtilisateur());
            } else {
                Utilisateur user2 = new Utilisateur();
                user2.setNomUtilisateur(request2.getNomUtilisateur());
                user2.setMotDePasse(encoder.encode(request2.getMotDePasse()));
                user2.setEmail(request2.getEmail());
                user2.setActif(true);
                user2.setRole(Role.CLIENT);
                Utilisateur savedUser2 = utilisateurRepository.save(user2);

                Client client2 = new Client();
                client2.setNom(request2.getNom());
                client2.setPrenom(request2.getPrenom());
                client2.setDate_inscription(LocalDateTime.now());
                client2.setAdresse(request2.getAdresse());
                client2.setTelephone(request2.getTelephone());
                client2.setUtilisateur(savedUser2);
                Client savedClient2 = clientRepository.save(client2);

                compteService.CompteClient(savedClient2);
                System.out.println(" Utilisateur 2 créé : " + savedUser2.getNomUtilisateur());
            }

            RegistrationRequest request3 = new RegistrationRequest();
            request3.setAdresse("yaounde");
            request3.setEmail("Romari@gmail.com");
            request3.setNom("kalf");
            request3.setPrenom("oklm");
            request3.setNomUtilisateur("kalfoklm");
            request3.setMotDePasse("password12345");
            request3.setTelephone("012345678");

            Optional<Utilisateur> utilisateurOptional3 = utilisateurRepository.findByEmail(request3.getEmail());
            if(utilisateurOptional3.isPresent()){
                System.out.println("cet utilisateur existe deja");
            }else{
                Utilisateur user3 = new Utilisateur();
                user3.setNomUtilisateur(request3.getNomUtilisateur());
                user3.setMotDePasse(encoder.encode(request3.getMotDePasse()));
                user3.setEmail(request3.getEmail());
                user3.setActif(true);
                user3.setRole(Role.ADMIN);
                Utilisateur savedUser3 = utilisateurRepository.save(user3);

                Client client3 = new Client();
                client3.setNom(request3.getNom());
                client3.setPrenom(request3.getPrenom());
                client3.setDate_inscription(LocalDateTime.now());
                client3.setAdresse(request3.getAdresse());
                client3.setTelephone(request3.getTelephone());
                client3.setUtilisateur(savedUser3);
                Client savedClient3 = clientRepository.save(client3);

                compteService.CompteClient(savedClient3);
                System.out.println("Utilisateur 3 créé : " + savedUser3.getNomUtilisateur());
            }

            RegistrationRequest request4 = new RegistrationRequest();
            request4.setAdresse("yaounde");
            request4.setEmail("NdoumberDuval@gmail.com");
            request4.setNom("lefa");
            request4.setPrenom("oklm");
            request4.setNomUtilisateur("lefaoklm");
            request4.setMotDePasse("password123456");
            request4.setTelephone("0123456578");

            Optional<Utilisateur> utilisateurOptional4 = utilisateurRepository.findByEmail(request4.getEmail());
            if(utilisateurOptional4.isPresent()){
                System.out.println("cet utilisateur existe deja");
            }else {
                Utilisateur user4 = new Utilisateur();
                user4.setNomUtilisateur(request4.getNomUtilisateur());
                user4.setMotDePasse(encoder.encode(request4.getMotDePasse()));
                user4.setEmail(request4.getEmail());
                user4.setActif(true);
                user4.setRole(Role.CAISSIER);
                Utilisateur savedUser4 = utilisateurRepository.save(user4);

                Client client4 = new Client();
                client4.setNom(request4.getNom());
                client4.setPrenom(request4.getPrenom());
                client4.setDate_inscription(LocalDateTime.now());
                client4.setAdresse(request4.getAdresse());
                client4.setTelephone(request4.getTelephone());
                client4.setUtilisateur(savedUser4);
                Client savedClient4 = clientRepository.save(client4);

                compteService.CompteClient(savedClient4);
                System.out.println("Utilisateur 3 créé : " + savedUser4.getNomUtilisateur());
            }
        };
    }
}