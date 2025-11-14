package com.example.demo.controller;

import com.example.demo.dto.CommandeFournisseurDTO;
import com.example.demo.dto.CreateCommandeRequest;
import com.example.demo.dto.PageResponse;
import com.example.demo.Enum.StatutCommande;
import com.example.demo.service.CommandeFournisseurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
@Tag(name = "Commandes Fournisseurs", description = "Gestion des commandes fournisseurs")
public class CommandeFournisseurController {

    private final CommandeFournisseurService commandeService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle commande fournisseur")
    public ResponseEntity<CommandeFournisseurDTO> create(@Valid @RequestBody CreateCommandeRequest request) {
        CommandeFournisseurDTO created = commandeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une commande")
    public ResponseEntity<CommandeFournisseurDTO> update(@PathVariable Long id,
                                                         @Valid @RequestBody CommandeFournisseurDTO dto) {
        CommandeFournisseurDTO updated = commandeService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/valider")
    @Operation(summary = "Valider une commande")
    public ResponseEntity<CommandeFournisseurDTO> valider(@PathVariable Long id) {
        CommandeFournisseurDTO validated = commandeService.validerCommande(id);
        return ResponseEntity.ok(validated);
    }

    @PatchMapping("/{id}/livrer")
    @Operation(summary = "Marquer une commande comme livrée")
    public ResponseEntity<CommandeFournisseurDTO> livrer(@PathVariable Long id) {
        CommandeFournisseurDTO delivered = commandeService.livrerCommande(id);
        return ResponseEntity.ok(delivered);
    }

    @PatchMapping("/{id}/annuler")
    @Operation(summary = "Annuler une commande")
    public ResponseEntity<CommandeFournisseurDTO> annuler(@PathVariable Long id) {
        CommandeFournisseurDTO cancelled = commandeService.annulerCommande(id);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une commande par ID")
    public ResponseEntity<CommandeFournisseurDTO> getById(@PathVariable Long id) {
        CommandeFournisseurDTO commande = commandeService.getById(id);
        return ResponseEntity.ok(commande);
    }

    @GetMapping
    @Operation(summary = "Obtenir toutes les commandes avec pagination")
    public ResponseEntity<PageResponse<CommandeFournisseurDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCommande") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<CommandeFournisseurDTO> response = commandeService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une commande")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

