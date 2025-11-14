package com.example.demo.controller;

import com.example.demo.dto.MouvementStockDTO;
import com.example.demo.dto.PageResponse;
import com.example.demo.Enum.TypeMouvement;
import com.example.demo.service.MouvementStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mouvements-stock")
@RequiredArgsConstructor
@Tag(name = "Mouvements de Stock", description = "Gestion des mouvements de stock")
public class MouvementStockController {

    private final MouvementStockService mouvementService;

    @GetMapping
    @Operation(summary = "Obtenir tous les mouvements avec pagination")
    public ResponseEntity<PageResponse<MouvementStockDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateMouvement") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<MouvementStockDTO> response = mouvementService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un mouvement par ID")
    public ResponseEntity<MouvementStockDTO> getById(@PathVariable Long id) {
        MouvementStockDTO mouvement = mouvementService.getById(id);
        return ResponseEntity.ok(mouvement);
    }

    @GetMapping("/produit/{produitId}")
    @Operation(summary = "Obtenir les mouvements d'un produit")
    public ResponseEntity<PageResponse<MouvementStockDTO>> getByProduit(
            @PathVariable Long produitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateMouvement").descending());
        PageResponse<MouvementStockDTO> response = mouvementService.getByProduit(produitId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Obtenir les mouvements par type")
    public ResponseEntity<PageResponse<MouvementStockDTO>> getByType(
            @PathVariable TypeMouvement type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateMouvement").descending());
        PageResponse<MouvementStockDTO> response = mouvementService.getByType(type, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/commande/{commandeId}")
    @Operation(summary = "Obtenir les mouvements d'une commande")
    public ResponseEntity<PageResponse<MouvementStockDTO>> getByCommande(
            @PathVariable Long commandeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateMouvement").descending());
        PageResponse<MouvementStockDTO> response = mouvementService.getByCommande(commandeId, pageable);
        return ResponseEntity.ok(response);
    }


}

