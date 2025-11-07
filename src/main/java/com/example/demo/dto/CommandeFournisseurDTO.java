package com.example.demo.dto;

import com.example.demo.Enum.StatutCommande;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeFournisseurDTO {
    private Long id;

    @NotNull(message = "La date de commande est obligatoire")
    private LocalDate dateCommande;

    private StatutCommande statut;
    private BigDecimal montantTotal;

    @NotNull(message = "Le fournisseur est obligatoire")
    private Long fournisseurId;

    private String fournisseurSociete;

    private List<LigneCommandeDTO> lignesCommande;
}
