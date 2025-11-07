package com.example.demo.repository;

import com.example.demo.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    Page<Produit> findByCategorie(String categorie, Pageable pageable);

    @Query("SELECT p FROM Produit p WHERE " +
            "LOWER(p.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.categorie) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Produit> searchProduits(@Param("search") String search, Pageable pageable);

    @Query("SELECT p FROM Produit p WHERE p.stockActuel < :seuil")
    List<Produit> findProduitsStockFaible(@Param("seuil") Integer seuil);

    List<Produit> findByIdIn(List<Long> ids);
}