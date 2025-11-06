package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_commande")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private CommandeFournisseur commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private Integer quantite;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(precision = 10, scale = 2)
    private BigDecimal sousTotal;

    @PrePersist
    @PreUpdate
    public void calculateSousTotal() {
        if (quantite != null && prixUnitaire != null) {
            this.sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
        }
    }
}