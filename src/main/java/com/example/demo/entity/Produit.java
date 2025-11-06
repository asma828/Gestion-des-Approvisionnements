package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    private String categorie;

    @Column(nullable = false)
    @Builder.Default
    private Integer stockActuel = 0;

    @Column(precision = 10, scale = 2)
    private BigDecimal coutMoyenPondere; // Pour CUMP

    @ManyToMany(mappedBy = "produits")
    @ToString.Exclude
    @Builder.Default
    private List<CommandeFournisseur> commandes = new ArrayList<>();

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<MouvementStock> mouvements = new ArrayList<>();
}
