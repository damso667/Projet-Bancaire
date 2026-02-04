package com.example.GestionBanque.repository;

import com.example.GestionBanque.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface  TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCompteSourceNumeroCompteOrderByDateTransactionDesc(String numeroCompte);
    @Query("SELECT t FROM Transaction t WHERE t.libelle LIKE %:nomCaissier% AND t.dateTransaction >= :debut")
    List<Transaction> findTransactionsDuJourParCaissier(
            @Param("nomCaissier") String nomCaissier,
            @Param("debut") LocalDateTime debut
    );
}
