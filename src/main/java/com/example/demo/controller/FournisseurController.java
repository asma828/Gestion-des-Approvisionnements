package com.example.demo.controller;

import com.example.demo.dto.FournisseurDTO;
import com.example.demo.dto.PageResponse;
import com.example.demo.service.FournisseurService;
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

@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
@Tag(name = "Fournisseurs", description = "Gestion des fournisseurs")
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @PostMapping
    @Operation(summary = "Créer un nouveau fournisseur")
    public ResponseEntity<FournisseurDTO> create(@Valid @RequestBody FournisseurDTO dto) {
        FournisseurDTO created = fournisseurService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un fournisseur")
    public ResponseEntity<FournisseurDTO> update(@PathVariable Long id,
                                                 @Valid @RequestBody FournisseurDTO dto) {
        FournisseurDTO updated = fournisseurService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un fournisseur par ID")
    public ResponseEntity<FournisseurDTO> getById(@PathVariable Long id) {
        FournisseurDTO fournisseur = fournisseurService.getById(id);
        return ResponseEntity.ok(fournisseur);
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les fournisseurs avec pagination")
    public ResponseEntity<PageResponse<FournisseurDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<FournisseurDTO> response = fournisseurService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des fournisseurs")
    public ResponseEntity<PageResponse<FournisseurDTO>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PageResponse<FournisseurDTO> response = fournisseurService.search(query, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un fournisseur")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fournisseurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
