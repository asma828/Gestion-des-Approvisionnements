package com.example.demo.service;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.ProduitDTO;
import com.example.demo.entity.Fournisseur;
import com.example.demo.entity.Produit;
import com.example.demo.mapper.ProduitMapper;
import com.example.demo.repository.FournisseurRepository;
import com.example.demo.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;

    public ProduitDTO create(ProduitDTO dto) {
        log.info("Creating new produit: {}", dto.getNom());

        Produit produit = produitMapper.toEntity(dto);

        // Set default stock if null
        if (produit.getStockActuel() == null) {
            produit.setStockActuel(0);
        }

        // Automatically calculate CUMP if stock > 0
        if (produit.getStockActuel() > 0 && produit.getPrixUnitaire() != null) {
            produit.setCoutMoyenPondere(produit.getPrixUnitaire());
        } else {
            produit.setCoutMoyenPondere(BigDecimal.ZERO);
        }

        produit = produitRepository.save(produit);

        log.info("Produit created with ID: {}, CUMP: {}", produit.getId(), produit.getCoutMoyenPondere());
        return produitMapper.toDTO(produit);
    }


    public ProduitDTO update(Long id, ProduitDTO dto) {
        log.info("Updating produit with ID: {}", id);

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        Integer stockActuel = produit.getStockActuel();

        BigDecimal coutActuel = produit.getCoutMoyenPondere() != null ? produit.getCoutMoyenPondere() : BigDecimal.ZERO;

        produit.setNom(dto.getNom());
        produit.setDescription(dto.getDescription());
        produit.setCategorie(dto.getCategorie());

        //  Update stock (missing before!)
        if (dto.getStockActuel() != null) {
            produit.setStockActuel(dto.getStockActuel());
            stockActuel = dto.getStockActuel();
        }

        // Recalculate CUMP if prixUnitaire changed
        if (dto.getPrixUnitaire() != null && dto.getPrixUnitaire().compareTo(produit.getPrixUnitaire()) != 0 && dto.getStockActuel()!=null && dto.getStockActuel().compareTo(produit.getStockActuel())!=0) {
            BigDecimal totalCoutActuel = coutActuel.multiply(BigDecimal.valueOf(stockActuel));
            BigDecimal totalNouveauCout = dto.getPrixUnitaire().multiply(BigDecimal.valueOf(stockActuel));
            BigDecimal nouveauCoutMoyen = totalCoutActuel.add(totalNouveauCout)
                    .divide(BigDecimal.valueOf(stockActuel), 2, BigDecimal.ROUND_HALF_UP);

            produit.setCoutMoyenPondere(nouveauCoutMoyen);
        }

        produit.setPrixUnitaire(dto.getPrixUnitaire());

        produit = produitRepository.save(produit);

        return produitMapper.toDTO(produit);
    }



    @Transactional(readOnly = true)
    public ProduitDTO getById(Long id) {
        log.info("Fetching produit with ID: {}", id);

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        return produitMapper.toDTO(produit);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProduitDTO> getAll(Pageable pageable) {
        log.info("Fetching all produits with pagination");

        Page<Produit> page = produitRepository.findAll(pageable);
        return buildPageResponse(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProduitDTO> search(String search, Pageable pageable) {
        log.info("Searching produits with query: {}", search);

        Page<Produit> page = produitRepository.searchProduits(search, pageable);
        return buildPageResponse(page);
    }

    public void delete(Long id) {
        log.info("Deleting produit with ID: {}", id);

        if (!produitRepository.existsById(id)) {
            throw new RuntimeException("Produit non trouvé avec l'ID: " + id);
        }

        produitRepository.deleteById(id);
        log.info("Produit deleted successfully");
    }

    protected void updateStock(Long produitId, Integer quantite) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        produit.setStockActuel(produit.getStockActuel() + quantite);
        produitRepository.save(produit);
    }

    protected void updateCoutMoyenPondere(Long produitId, BigDecimal nouveauCout, Integer quantite) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        Integer stockActuel = produit.getStockActuel();
        BigDecimal coutActuel = produit.getCoutMoyenPondere() != null
                ? produit.getCoutMoyenPondere()
                : BigDecimal.ZERO;

        BigDecimal totalCoutActuel = coutActuel.multiply(BigDecimal.valueOf(stockActuel));
        BigDecimal totalNouveauCout = nouveauCout.multiply(BigDecimal.valueOf(quantite));
        BigDecimal nouveauStockTotal = BigDecimal.valueOf(stockActuel + quantite);

        BigDecimal nouveauCoutMoyen = totalCoutActuel.add(totalNouveauCout)
                .divide(nouveauStockTotal, 2, BigDecimal.ROUND_HALF_UP);

        produit.setCoutMoyenPondere(nouveauCoutMoyen);
        produitRepository.save(produit);
    }

    private PageResponse<ProduitDTO> buildPageResponse(Page<Produit> page) {
        return PageResponse.<ProduitDTO>builder()
                .content(produitMapper.toDTOList(page.getContent()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }


}