package com.example.demo.dto;

import com.example.demo.Enum.TypeMouvement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementStockDTO {
    private Long id;
    private LocalDateTime dateMouvement;

    @NotNull(message = "Le type de mouvement est obligatoire")
    private TypeMouvement type;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private Integer quantite;

    private BigDecimal coutUnitaire;

    @NotNull(message = "Le produit est obligatoire")
    private Long produitId;

    private String produitNom;
    private Long commandeFournisseurId;
    private String reference;
    private String commentaire;
}

