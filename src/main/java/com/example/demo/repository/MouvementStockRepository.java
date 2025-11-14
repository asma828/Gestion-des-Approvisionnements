package com.example.demo.repository;

import com.example.demo.entity.MouvementStock;
import com.example.demo.Enum.TypeMouvement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

    Page<MouvementStock> findByProduitId(Long produitId, Pageable pageable);

    Page<MouvementStock> findByType(TypeMouvement type, Pageable pageable);

    Page<MouvementStock> findByCommandeFournisseurId(Long commandeId, Pageable pageable);

    @Query("SELECT m FROM MouvementStock m WHERE m.produit.id = :produitId ORDER BY m.dateMouvement DESC")
    Page<MouvementStock> findHistoriqueByProduit(@Param("produitId") Long produitId, Pageable pageable);
}
