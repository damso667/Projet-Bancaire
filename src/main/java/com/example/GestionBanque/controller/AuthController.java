package com.example.GestionBanque.controller;

import com.example.GestionBanque.config.JwtUtil;
import com.example.GestionBanque.dto.JwtResponse;
import com.example.GestionBanque.dto.LoginRequest;
import com.example.GestionBanque.dto.RegistrationRequest;
import com.example.GestionBanque.enums.Role;
import com.example.GestionBanque.models.Client;
import com.example.GestionBanque.models.Compte;
import com.example.GestionBanque.models.Utilisateur;
import com.example.GestionBanque.repository.ClientRepository;
import com.example.GestionBanque.repository.UtilisateurRepository;
import com.example.GestionBanque.services.CompteService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Pour autoriser Angular en local
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder encoder;
    private final CompteService compteService;
    private final JwtUtil jwtUtils;

    // 1. CONNEXION
    @PostMapping("/connexion")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // Vérification du login/password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getMotDePasse()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Génération du Token
        String jwt = jwtUtils.generateToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));
    }

    // 2. INSCRIPTION (Simulation Client)
    @PostMapping("/inscription")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest signUpRequest) {

        if (utilisateurRepository.findByNomUtilisateurOrEmail(signUpRequest.getNomUtilisateur(),signUpRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Erreur: Ce nom d'utilisateur est déjà pris!");
        }

        // Création de l'Utilisateur
        Utilisateur user = new Utilisateur();
        user.setNomUtilisateur(signUpRequest.getNomUtilisateur());
        // Dans ton AuthController
        user.setMotDePasse(encoder.encode(signUpRequest.getMotDePasse()));
        user.setEmail(signUpRequest.getEmail());
        user.setActif(true); // En local, on l'active directement pour tester [cite: 3]
        user.setRole(Role.CLIENT);

        Utilisateur savedUser = utilisateurRepository.save(user);

        // Création automatique du profil Client lié
        Client client = new Client();
        client.setNom(signUpRequest.getNom());
        client.setPrenom(signUpRequest.getPrenom());
        client.setDate_inscription(LocalDateTime.now());
        client.setAdresse(signUpRequest.getAdresse());
        client.setTelephone(signUpRequest.getTelephone());
        client.setUtilisateur(savedUser); // Liaison 1-à-1
        Client saveClient = clientRepository.save(client);

        compteService.CompteClient(saveClient);

        Compte compte = new Compte();

        return ResponseEntity.ok("Inscription réussie : Compte utilisateur avec un numero de compte: " +client.getComptes()+ "  , profil client et compte bancaire créés !");
    }
}
