package com.example.demo.repository;

import com.example.demo.entity.CommandeFournisseur;
import com.example.demo.Enum.StatutCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CommandeFournisseurRepository extends JpaRepository<CommandeFournisseur, Long> {

    Page<CommandeFournisseur> findByFournisseurId(Long fournisseurId, Pageable pageable);

    Page<CommandeFournisseur> findByStatut(StatutCommande statut, Pageable pageable);

    @Query("SELECT c FROM CommandeFournisseur c WHERE c.dateCommande BETWEEN :startDate AND :endDate")
    Page<CommandeFournisseur> findByDateCommandeBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT c FROM CommandeFournisseur c WHERE " +
            "c.fournisseur.id = :fournisseurId AND c.statut = :statut")
    List<CommandeFournisseur> findByFournisseurIdAndStatut(
            @Param("fournisseurId") Long fournisseurId,
            @Param("statut") StatutCommande statut
    );

    @Query("SELECT c FROM CommandeFournisseur c JOIN FETCH c.fournisseur WHERE c.id = :id")
    CommandeFournisseur findByIdWithFournisseur(@Param("id") Long id);

    @Query("SELECT c FROM CommandeFournisseur c " +
            "LEFT JOIN FETCH c.lignesCommande lc " +
            "LEFT JOIN FETCH lc.produit " +
            "WHERE c.id = :id")
    CommandeFournisseur findByIdWithDetails(@Param("id") Long id);
}