package com.example.demo.service;

import com.example.demo.dto.MouvementStockDTO;
import com.example.demo.dto.PageResponse;
import com.example.demo.entity.MouvementStock;
import com.example.demo.Enum.TypeMouvement;
import com.example.demo.mapper.MouvementStockMapper;
import com.example.demo.repository.MouvementStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MouvementStockService {

    private final MouvementStockRepository mouvementRepository;
    private final MouvementStockMapper mouvementMapper;

    public PageResponse<MouvementStockDTO> getAll(Pageable pageable) {
        log.info("Fetching all mouvements with pagination");

        Page<MouvementStock> page = mouvementRepository.findAll(pageable);
        return buildPageResponse(page);
    }

    public PageResponse<MouvementStockDTO> getByProduit(Long produitId, Pageable pageable) {
        log.info("Fetching mouvements for product ID: {}", produitId);

        Page<MouvementStock> page = mouvementRepository.findByProduitId(produitId, pageable);
        return buildPageResponse(page);
    }

    public PageResponse<MouvementStockDTO> getByType(TypeMouvement type, Pageable pageable) {
        log.info("Fetching mouvements by type: {}", type);

        Page<MouvementStock> page = mouvementRepository.findByType(type, pageable);
        return buildPageResponse(page);
    }

    public PageResponse<MouvementStockDTO> getByCommande(Long commandeId, Pageable pageable) {
        log.info("Fetching mouvements for commande ID: {}", commandeId);

        Page<MouvementStock> page = mouvementRepository.findByCommandeFournisseurId(commandeId, pageable);
        return buildPageResponse(page);
    }

    public MouvementStockDTO getById(Long id) {
        log.info("Fetching mouvement with ID: {}", id);

        MouvementStock mouvement = mouvementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mouvement non trouvé avec l'ID: " + id));

        return mouvementMapper.toDTO(mouvement);
    }

    private PageResponse<MouvementStockDTO> buildPageResponse(Page<MouvementStock> page) {
        return PageResponse.<MouvementStockDTO>builder()
                .content(mouvementMapper.toDTOList(page.getContent()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

}
