package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.Enum.TypeMouvement;
import com.example.demo.repository.MouvementStockRepository;
import com.example.demo.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockService {

    private final MouvementStockRepository mouvementRepository;
    private final ProduitRepository produitRepository;

    /**
     * Crée un mouvement d'entrée en stock et met à jour le CUMP
     * Formule CUMP: (Stock Ancien × CUMP Ancien + Quantité Entrée × Prix Entrée) / (Stock Ancien + Quantité Entrée)
     */
    public void creerMouvementEntree(Long produitId, Integer quantite, BigDecimal coutUnitaire,
                                     Long commandeId, String commentaire) {
        log.info("Creating stock entry movement for product ID: {}, quantity: {}", produitId, quantite);

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Sauvegarder l'ancien stock et CUMP pour le calcul
        Integer stockAvant = produit.getStockActuel();
        BigDecimal cumpAvant = produit.getCoutMoyenPondere() != null
                ? produit.getCoutMoyenPondere()
                : BigDecimal.ZERO;

        // Créer le mouvement d'entrée
        MouvementStock mouvement = MouvementStock.builder()
                .dateMouvement(LocalDateTime.now())
                .type(TypeMouvement.ENTREE)
                .quantite(quantite)
                .coutUnitaire(coutUnitaire)
                .produit(produit)
                .reference("CMD-" + commandeId)
                .commentaire(commentaire)
                .build();

        mouvementRepository.save(mouvement);


        log.info("Stock entry movement created for product ID: {} (no changes to product stock)", produitId);
    }

    /**
     * Crée un mouvement de sortie de stock
     * Le coût utilisé est le CUMP actuel du produit
     */
    public void creerMouvementSortie(Long produitId, Integer quantite, String reference, String commentaire) {
        log.info("Creating stock exit movement for product ID: {}, quantity: {}", produitId, quantite);

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Vérifier le stock disponible
        if (produit.getStockActuel() < quantite) {
            throw new RuntimeException(String.format(
                    "Stock insuffisant pour le produit '%s'. Disponible: %d, Demandé: %d",
                    produit.getNom(), produit.getStockActuel(), quantite));
        }

        // Utiliser le CUMP actuel pour la sortie
        BigDecimal coutUnitaire = produit.getCoutMoyenPondere() != null
                ? produit.getCoutMoyenPondere()
                : BigDecimal.ZERO;

        // Créer le mouvement de sortie
        MouvementStock mouvement = MouvementStock.builder()
                .dateMouvement(LocalDateTime.now())
                .type(TypeMouvement.SORTIE)
                .quantite(quantite)
                .coutUnitaire(coutUnitaire)
                .produit(produit)
                .reference(reference)
                .commentaire(commentaire)
                .build();

        mouvementRepository.save(mouvement);

        // Mettre à jour le stock actuel (le CUMP ne change pas lors d'une sortie)
        produit.setStockActuel(produit.getStockActuel() - quantite);
        produitRepository.save(produit);

        log.info("Stock exit movement created. New stock level: {}", produit.getStockActuel());
    }

    /**
     * Crée un mouvement d'ajustement de stock (inventaire, correction, etc.)
     * Peut être positif (ajout) ou négatif (retrait)
     */
    public void creerMouvementAjustement(Long produitId, Integer quantite, String reference, String commentaire) {
        log.info("Creating stock adjustment movement for product ID: {}, quantity: {}", produitId, quantite);

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Vérifier que l'ajustement négatif ne crée pas un stock négatif
        if (produit.getStockActuel() + quantite < 0) {
            throw new RuntimeException(String.format(
                    "Ajustement impossible. Stock actuel: %d, Ajustement: %d",
                    produit.getStockActuel(), quantite));
        }

        // Utiliser le CUMP actuel
        BigDecimal coutUnitaire = produit.getCoutMoyenPondere() != null
                ? produit.getCoutMoyenPondere()
                : BigDecimal.ZERO;

        // Créer le mouvement d'ajustement
        MouvementStock mouvement = MouvementStock.builder()
                .dateMouvement(LocalDateTime.now())
                .type(TypeMouvement.AJUSTEMENT)
                .quantite(quantite)
                .coutUnitaire(coutUnitaire)
                .produit(produit)
                .reference(reference)
                .commentaire(commentaire)
                .build();

        mouvementRepository.save(mouvement);

        // Mettre à jour le stock actuel
        produit.setStockActuel(produit.getStockActuel() + quantite);
        produitRepository.save(produit);

        log.info("Stock adjustment movement created. New stock level: {}", produit.getStockActuel());
    }

    /**
     * Calcule le nouveau CUMP après une entrée en stock
     * Formule: (Stock Ancien × CUMP Ancien + Quantité Entrée × Prix Entrée) / (Stock Ancien + Quantité Entrée)
     */
    private BigDecimal calculerCUMP(Integer stockAvant, BigDecimal cumpAvant,
                                    Integer quantiteEntree, BigDecimal prixEntree) {

        // Si le stock était vide, le nouveau CUMP est simplement le prix d'entrée
        if (stockAvant == 0) {
            log.debug("Stock was empty, new CUMP = entry price: {}", prixEntree);
            return prixEntree;
        }

        // Calcul du coût total de l'ancien stock
        BigDecimal coutTotalAvant = cumpAvant.multiply(BigDecimal.valueOf(stockAvant));

        // Calcul du coût total de la nouvelle entrée
        BigDecimal coutTotalEntree = prixEntree.multiply(BigDecimal.valueOf(quantiteEntree));

        // Calcul du nouveau stock total
        BigDecimal stockTotalApres = BigDecimal.valueOf(stockAvant + quantiteEntree);

        // Calcul du nouveau CUMP
        BigDecimal nouveauCUMP = coutTotalAvant.add(coutTotalEntree)
                .divide(stockTotalApres, 2, RoundingMode.HALF_UP);

        log.debug("CUMP calculation: ({} × {}) + ({} × {}) / {} = {}",
                stockAvant, cumpAvant, quantiteEntree, prixEntree, stockTotalApres, nouveauCUMP);

        return nouveauCUMP;
    }
}
