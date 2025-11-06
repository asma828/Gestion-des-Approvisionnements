package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fournisseurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String societe;

    private String adresse;

    private String contact;

    @Column(unique = true)
    private String email;

    private String telephone;

    private String ville;

    @Column(unique = true)
    private String ice; // Identifiant Commun de l'Entreprise

    @OneToMany(mappedBy = "fournisseur", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    private List<CommandeFournisseur> commandes = new ArrayList<>();
}