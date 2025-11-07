package com.example.demo.repository;
import com.example.demo.entity.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    List<LigneCommande> findByCommandeId(Long commandeId);

    List<LigneCommande> findByProduitId(Long produitId);

    @Query("SELECT lc FROM LigneCommande lc WHERE lc.commande.id = :commandeId")
    List<LigneCommande> findLignesByCommandeId(@Param("commandeId") Long commandeId);
}
