package com.example.demo.service;

import com.example.demo.dto.FournisseurDTO;
import com.example.demo.dto.PageResponse;
import com.example.demo.entity.Fournisseur;
import com.example.demo.mapper.FournisseurMapper;
import com.example.demo.repository.CommandeFournisseurRepository;
import com.example.demo.repository.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final FournisseurMapper fournisseurMapper;
    private final CommandeFournisseurRepository commandeFournisseurRepository;

    public FournisseurDTO create(FournisseurDTO dto) {
        log.info("Creating new fournisseur: {}", dto.getSociete());

        if (dto.getEmail() != null && fournisseurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Un fournisseur avec cet email existe déjà");
        }

        if (dto.getIce() != null && fournisseurRepository.existsByIce(dto.getIce())) {
            throw new RuntimeException("Un fournisseur avec cet ICE existe déjà");
        }

        Fournisseur fournisseur = fournisseurMapper.toEntity(dto);
        fournisseur = fournisseurRepository.save(fournisseur);

        log.info("Fournisseur created with ID: {}", fournisseur.getId());
        return fournisseurMapper.toDTO(fournisseur);
    }

    public FournisseurDTO update(Long id, FournisseurDTO dto) {
        log.info("Updating fournisseur with ID: {}", id);

        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'ID: " + id));

        fournisseurMapper.updateEntityFromDTO(dto, fournisseur);
        fournisseur = fournisseurRepository.save(fournisseur);

        return fournisseurMapper.toDTO(fournisseur);
    }

    @Transactional(readOnly = true)
    public FournisseurDTO getById(Long id) {
        log.info("Fetching fournisseur with ID: {}", id);

        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'ID: " + id));

        return fournisseurMapper.toDTO(fournisseur);
    }

    @Transactional(readOnly = true)
    public PageResponse<FournisseurDTO> getAll(Pageable pageable) {
        log.info("Fetching all fournisseurs with pagination");

        Page<Fournisseur> page = fournisseurRepository.findAll(pageable);
        return buildPageResponse(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<FournisseurDTO> search(String search, Pageable pageable) {
        log.info("Searching fournisseurs with query: {}", search);

        Page<Fournisseur> page = fournisseurRepository.searchFournisseurs(search, pageable);
        return buildPageResponse(page);
    }

    public void delete(Long id) {
        log.info("Deleting fournisseur with ID: {}", id);

        if (!fournisseurRepository.existsById(id)) {
            throw new RuntimeException("Fournisseur non trouvé avec l'ID: " + id);
        }

        fournisseurRepository.deleteById(id);
        log.info("Fournisseur deleted successfully");
    }

    private PageResponse<FournisseurDTO> buildPageResponse(Page<Fournisseur> page) {
        return PageResponse.<FournisseurDTO>builder()
                .content(fournisseurMapper.toDTOList(page.getContent()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}