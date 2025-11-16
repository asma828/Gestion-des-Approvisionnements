package com.example.demo.service;

import com.example.demo.dto.ProduitDTO;
import com.example.demo.entity.Produit;
import com.example.demo.mapper.ProduitMapper;
import com.example.demo.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - ProduitService")
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ProduitMapper produitMapper;

    @InjectMocks
    private ProduitService produitService;

    private Produit produit;
    private ProduitDTO produitDTO;

    @BeforeEach
    void setUp() {
        produit = Produit.builder()
                .id(1L)
                .nom("Tissu Coton Blanc")
                .prixUnitaire(new BigDecimal("45.50"))
                .categorie("Tissu")
                .stockActuel(100)
                .coutMoyenPondere(new BigDecimal("45.00"))
                .build();

        produitDTO = ProduitDTO.builder()
                .id(1L)
                .nom("Tissu Coton Blanc")
                .prixUnitaire(new BigDecimal("45.50"))
                .categorie("Tissu")
                .stockActuel(100)
                .coutMoyenPondere(new BigDecimal("45.00"))
                .build();
    }

    @Test
    @DisplayName("Créer un produit - Succès")
    void testCreateProduit_Success() {
        // Given
        when(produitMapper.toEntity(produitDTO)).thenReturn(produit);
        when(produitRepository.save(any(Produit.class))).thenReturn(produit);
        when(produitMapper.toDTO(produit)).thenReturn(produitDTO);

        // When
        ProduitDTO result = produitService.create(produitDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNom()).isEqualTo("Tissu Coton Blanc");
        assertThat(result.getStockActuel()).isEqualTo(100);
        verify(produitRepository).save(any(Produit.class));
    }

    @Test
    @DisplayName("Obtenir un produit par ID - Succès")
    void testGetById_Success() {
        // Given
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(produitMapper.toDTO(produit)).thenReturn(produitDTO);

        // When
        ProduitDTO result = produitService.getById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNom()).isEqualTo("Tissu Coton Blanc");
        assertThat(result.getPrixUnitaire()).isEqualByComparingTo(new BigDecimal("45.50"));
    }
}