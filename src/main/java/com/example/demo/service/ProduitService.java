package com.example.demo.service;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.ProduitDTO;
import com.example.demo.entity.Produit;
import com.example.demo.mapper.ProduitMapper;
import com.example.demo.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
        if (produit.getStockActuel() == null) {
            produit.setStockActuel(0);
        }

        produit = produitRepository.save(produit);

        log.info("Produit created with ID: {}", produit.getId());
        return produitMapper.toDTO(produit);
    }

    public ProduitDTO update(Long id, ProduitDTO dto) {
        log.info("Updating produit with ID: {}", id);

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + id));

        produitMapper.updateEntityFromDTO(dto, produit);
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

    @Transactional(readOnly = true)
    public PageResponse<ProduitDTO> getByCategorie(String categorie, Pageable pageable) {
        log.info("Fetching produits by categorie: {}", categorie);

        Page<Produit> page = produitRepository.findByCategorie(categorie, pageable);
        return buildPageResponse(page);
    }

    @Transactional(readOnly = true)
    public List<ProduitDTO> getProduitsStockFaible(Integer seuil) {
        log.info("Fetching produits with stock below: {}", seuil);

        List<Produit> produits = produitRepository.findProduitsStockFaible(seuil);
        return produitMapper.toDTOList(produits);
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