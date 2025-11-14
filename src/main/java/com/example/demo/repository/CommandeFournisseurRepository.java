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

    @Query("SELECT c FROM CommandeFournisseur c " +
            "LEFT JOIN FETCH c.lignesCommande lc " +
            "LEFT JOIN FETCH lc.produit " +
            "WHERE c.id = :id")
    CommandeFournisseur findByIdWithDetails(@Param("id") Long id);
}