package com.example.demo.service;

import com.example.demo.dto.CommandeFournisseurDTO;
import com.example.demo.dto.CreateCommandeRequest;
import com.example.demo.dto.LigneCommandeDTO;
import com.example.demo.dto.PageResponse;
import com.example.demo.entity.*;
import com.example.demo.Enum.StatutCommande;
import com.example.demo.mapper.CommandeFournisseurMapper;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommandeFournisseurService {

    private final CommandeFournisseurRepository commandeRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ProduitRepository produitRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final CommandeFournisseurMapper commandeMapper;
    private final StockService stockService;

    public CommandeFournisseurDTO create(CreateCommandeRequest request) {
        log.info("Creating new commande for fournisseur ID: {}", request.getFournisseurId());

        Fournisseur fournisseur = fournisseurRepository.findById(request.getFournisseurId())
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));

        CommandeFournisseur commande = CommandeFournisseur.builder()
                .dateCommande(request.getDateCommande())
                .statut(StatutCommande.EN_ATTENTE)
                .fournisseur(fournisseur)
                .lignesCommande(new ArrayList<>())
                .build();

        BigDecimal montantTotal = BigDecimal.ZERO;

        for (LigneCommandeDTO ligneDTO : request.getLignesCommande()) {
            Produit produit = produitRepository.findById(ligneDTO.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + ligneDTO.getProduitId()));

            LigneCommande ligne = LigneCommande.builder()
                    .commande(commande)
                    .produit(produit)
                    .quantite(ligneDTO.getQuantite())
                    .prixUnitaire(ligneDTO.getPrixUnitaire())
                    .build();

            ligne.calculateSousTotal();
            commande.getLignesCommande().add(ligne);
            montantTotal = montantTotal.add(ligne.getSousTotal());
        }

        commande.setMontantTotal(montantTotal);
        commande = commandeRepository.save(commande);

        // Créer les mouvements d'ENTREE dès la création de la commande
        for (LigneCommande ligne : commande.getLignesCommande()) {
            stockService.creerMouvementEntree(
                    ligne.getProduit().getId(),
                    ligne.getQuantite(),
                    ligne.getPrixUnitaire(),
                    commande.getId(),
                    "Commande créée #" + commande.getId()
            );
        }

        log.info("Commande created with ID: {} and total amount: {}", commande.getId(), montantTotal);
        return commandeMapper.toDTO(commande);
    }

    public CommandeFournisseurDTO update(Long id, CommandeFournisseurDTO dto) {
        log.info("Updating commande with ID: {}", id);

        CommandeFournisseur commande = commandeRepository.findByIdWithDetails(id);
        if (commande == null) {
            throw new RuntimeException("Commande non trouvée avec l'ID: " + id);
        }

        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new RuntimeException("Impossible de modifier une commande déjà livrée");
        }

        if (commande.getStatut() == StatutCommande.ANNULEE) {
            throw new RuntimeException("Impossible de modifier une commande annulée");
        }

        commande.setDateCommande(dto.getDateCommande());
        commande = commandeRepository.save(commande);

        return commandeMapper.toDTO(commande);
    }

    public CommandeFournisseurDTO validerCommande(Long id) {
        log.info("Validating commande with ID: {}", id);

        CommandeFournisseur commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new RuntimeException("Seules les commandes en attente peuvent être validées");
        }

        commande.setStatut(StatutCommande.VALIDEE);
        commande = commandeRepository.save(commande);

        return commandeMapper.toDTO(commande);
    }

    public CommandeFournisseurDTO livrerCommande(Long id) {
        log.info("Delivering commande with ID: {}", id);

        CommandeFournisseur commande = commandeRepository.findByIdWithDetails(id);
        if (commande == null) {
            throw new RuntimeException("Commande non trouvée");
        }

        if (commande.getStatut() != StatutCommande.VALIDEE) {
            throw new RuntimeException("Seules les commandes validées peuvent être livrées");
        }

        commande.setStatut(StatutCommande.LIVREE);
        commande = commandeRepository.save(commande);

        // Create stock movements for each product
        for (LigneCommande ligne : commande.getLignesCommande()) {
            stockService.creerMouvementSortie(
                    ligne.getProduit().getId(),
                    ligne.getQuantite(),
                    "CMD-" + commande.getId(),
                    "Livraison commande #" + commande.getId()
            );
        }

        log.info("Commande delivered and stock movements created");
        return commandeMapper.toDTO(commande);
    }

    public CommandeFournisseurDTO annulerCommande(Long id) {
        log.info("Cancelling commande with ID: {}", id);

        CommandeFournisseur commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new RuntimeException("Impossible d'annuler une commande déjà livrée");
        }

        commande.setStatut(StatutCommande.ANNULEE);
        commande = commandeRepository.save(commande);

        return commandeMapper.toDTO(commande);
    }

    @Transactional(readOnly = true)
    public CommandeFournisseurDTO getById(Long id) {
        log.info("Fetching commande with ID: {}", id);

        CommandeFournisseur commande = commandeRepository.findByIdWithDetails(id);
        if (commande == null) {
            throw new RuntimeException("Commande non trouvée avec l'ID: " + id);
        }

        return commandeMapper.toDTO(commande);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommandeFournisseurDTO> getAll(Pageable pageable) {
        log.info("Fetching all commandes with pagination");

        Page<CommandeFournisseur> page = commandeRepository.findAll(pageable);
        return buildPageResponse(page);
    }

    public void delete(Long id) {
        log.info("Deleting commande with ID: {}", id);

        CommandeFournisseur commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new RuntimeException("Impossible de supprimer une commande livrée");
        }

        commandeRepository.deleteById(id);
        log.info("Commande deleted successfully");
    }

    private PageResponse<CommandeFournisseurDTO> buildPageResponse(Page<CommandeFournisseur> page) {
        return PageResponse.<CommandeFournisseurDTO>builder()
                .content(commandeMapper.toDTOList(page.getContent()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
