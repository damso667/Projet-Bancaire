package com.example.GestionBanque.services;

import com.example.GestionBanque.dto.VirementRequest;
import com.example.GestionBanque.enums.TypeCompte;
import com.example.GestionBanque.enums.TypeTransaction;
import com.example.GestionBanque.models.Client;
import com.example.GestionBanque.models.Compte;
import com.example.GestionBanque.models.Transaction;
import com.example.GestionBanque.repository.CompteRepository;
import com.example.GestionBanque.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Transactional
public class CompteService {

    private CompteRepository compteRepository;
    private TransactionRepository transactionRepository;

    public Compte CompteClient(Client client) {
        Compte compte = new Compte();

        // Génération d'un numéro de compte simple pour ton projet local
        String numeroGenere = "BANQUE-" + System.currentTimeMillis();
        compte.setNumeroCompte(numeroGenere);
        compte.setSolde(0.0);
        compte.setDevise("EUR");
        compte.setDateCreation(LocalDateTime.now());
        compte.setTypeCompte(TypeCompte.COURANT);
        compte.setClient(client);
        return compteRepository.save(compte);
    }

    public void effectuerVirement(VirementRequest request) {
        // 1. Chercher les deux comptes
        Compte source = compteRepository.findByNumeroCompte(request.getNumeroCompteSource())
                .orElseThrow(() -> new RuntimeException("Compte source introuvable"));
        if (!source.actif) {
            throw new RuntimeException("Opération impossible : Ce compte est actuellement BLOQUÉ.");
        }

        Compte destination = compteRepository.findByNumeroCompte(request.getNumeroCompteDestination())
                .orElseThrow(() -> new RuntimeException("Compte destination introuvable"));

        // 2. Vérifier le solde
        if (source.getSolde() < request.getMontant()) {
            throw new RuntimeException("Solde insuffisant pour effectuer le virement");
        }

        // Opération mathématique du calul du solde
        source.setSolde(source.getSolde() - request.getMontant());
        destination.setSolde(destination.getSolde() + request.getMontant());

        // 4. Sauvegarde
        compteRepository.save(source);
        compteRepository.save(destination);
        enregistrerTransaction(source, -request.getMontant(), TypeTransaction.VIREMENT, "Virement De: " + request.getMontant() + " vers le compte " + source.getNumeroCompte());
        enregistrerTransaction(destination, request.getMontant(), TypeTransaction.RECEPTION, "Reception d'un montant de: " + request.getMontant() + " Fcfa provenant du compte: " + destination.getNumeroCompte());

    }

    @Transactional
    public void ajouterSolde(String numeroCompte, double montant) {
        //  Validation de sécurité
        if (montant <= 0) {
            throw new RuntimeException("Le montant du dépôt doit être positif");
        }

        //  Recherche du compte
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));

        //  Mise à jour
        compte.setSolde(compte.getSolde() + montant);

        //  Sauvegarde
        compteRepository.save(compte);
        enregistrerTransaction(compte, montant, TypeTransaction.DEPOT, "Rechargement compte");
    }



    public List<Transaction> obtenirHistorique(String numeroCompte) {
        return transactionRepository.findByCompteSourceNumeroCompteOrderByDateTransactionDesc(numeroCompte);
    }

    // methode l'admin pour lister tous les comptes
    public List<Compte> listerTousLesComptes() {
        return compteRepository.findAll();
    }

    //methode l'admin pour activer ou desactiver un compte
    @Transactional
    public void modifierStatutCompte(String numeroCompte, boolean actif) {
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        compte.setActif(actif);
        compteRepository.save(compte);
    }

    @Transactional
    public void retirerSolde(String numeroCompte, double montant) {
        if (montant <= 0) throw new RuntimeException("Montant invalide");

        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));

        // Sécurité : Compte bloqué ?
        if (!compte.getActif()) {
            throw new RuntimeException("Action impossible : Ce compte est bloqué.");
        }

        // Sécurité : Solde suffisant ?
        if (compte.getSolde() < montant) {
            throw new RuntimeException("Solde insuffisant pour ce retrait.");
        }

        compte.setSolde(compte.getSolde() - montant);
        compteRepository.save(compte);


        // Historique
        enregistrerTransaction(compte, -montant, TypeTransaction.RETRAIR_GUICHET, "Retrait au guichet");
    }

    public void AjouterSoldeGuicher(String numeroCompte, double montant,String cassier) {
        // verfircation du montant
        if (montant <= 0) {
            throw new RuntimeException("Le montant du dépôt doit être positif");
        }

        // Recherche du compte
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));

        //  Mise à jour du montant
        compte.setSolde(compte.getSolde() + montant);

        //  Sauvegarde du compte
        compteRepository.save(compte);
        enregistrerTransaction(compte, montant, TypeTransaction.DEPOT_GUICHET, "Rechargement compte au guichet");
    }

    public Map<String, Object> calculerBilanJournalier(String nomCaissier) {
        LocalDateTime ceMatin = LocalDate.from(Instant.now()).atStartOfDay();
        List<Transaction> transactions = transactionRepository.findTransactionsDuJourParCaissier(nomCaissier, ceMatin);

        double totalDepots = transactions.stream()
                .filter(t -> t.getMontant() > 0)
                .mapToDouble(Transaction::getMontant).sum();

        double totalRetraits = transactions.stream()
                .filter(t -> t.getMontant() < 0)
                .mapToDouble(t -> Math.abs(t.getMontant())).sum();

        Map<String, Object> bilan = new HashMap<>();
        bilan.put("caissier", nomCaissier);
        bilan.put("nbOperations", transactions.size());
        bilan.put("totalDepots", totalDepots);
        bilan.put("totalRetraits", totalRetraits);
        bilan.put("soldeCaisse", totalDepots - totalRetraits);

        return bilan;
    }

    //methode de sauvegarde des transactions
    private void enregistrerTransaction(Compte compte, Double montant, TypeTransaction type, String desc) {
        Transaction t = new Transaction();
        t.setCompteSource(compte);
        t.setMontant(montant);
        t.setTypeTransaction(type);
        t.setLibelle(desc);
        t.setDateTransaction( LocalDateTime.now());
        transactionRepository.save(t);
    }


}
