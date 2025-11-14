package com.example.demo.repository;

import com.example.demo.entity.Fournisseur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

    boolean existsByEmail(String email);

    boolean existsByIce(String ice);

    @Query("SELECT f FROM Fournisseur f WHERE " +
            "LOWER(f.societe) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(f.ville) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(f.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Fournisseur> searchFournisseurs(@Param("search") String search, Pageable pageable);
}