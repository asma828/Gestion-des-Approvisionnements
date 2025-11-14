package com.example.demo.controller;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.ProduitDTO;
import com.example.demo.service.ProduitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "Gestion des produits")
public class ProduitController {

    private final ProduitService produitService;

    @PostMapping
    @Operation(summary = "Créer un nouveau produit")
    public ResponseEntity<ProduitDTO> create(@Valid @RequestBody ProduitDTO dto) {
        ProduitDTO created = produitService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un produit")
    public ResponseEntity<ProduitDTO> update(@PathVariable Long id,
                                             @Valid @RequestBody ProduitDTO dto) {
        ProduitDTO updated = produitService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un produit par ID")
    public ResponseEntity<ProduitDTO> getById(@PathVariable Long id) {
        ProduitDTO produit = produitService.getById(id);
        return ResponseEntity.ok(produit);
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les produits avec pagination")
    public ResponseEntity<PageResponse<ProduitDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<ProduitDTO> response = produitService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des produits")
    public ResponseEntity<PageResponse<ProduitDTO>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ProduitDTO> response = produitService.search(query, pageable);
        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
