package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommandeRequest {
    @NotNull(message = "La date de commande est obligatoire")
    private LocalDate dateCommande;

    @NotNull(message = "Le fournisseur est obligatoire")
    private Long fournisseurId;

    @NotEmpty(message = "La commande doit contenir au moins un produit")
    @Valid
    private List<LigneCommandeDTO> lignesCommande;
}
