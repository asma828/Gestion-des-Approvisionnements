package com.example.demo.service;

import com.example.demo.dto.FournisseurDTO;
import com.example.demo.dto.PageResponse;
import com.example.demo.entity.Fournisseur;
import com.example.demo.mapper.FournisseurMapper;
import com.example.demo.repository.FournisseurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - FournisseurService")
class FournisseurServiceTest {

    @Mock
    private FournisseurRepository fournisseurRepository;

    @Mock
    private FournisseurMapper fournisseurMapper;

    @InjectMocks
    private FournisseurService fournisseurService;

    private Fournisseur fournisseur;
    private FournisseurDTO fournisseurDTO;

    @BeforeEach
    void setUp() {
        fournisseur = Fournisseur.builder()
                .id(1L)
                .societe("Textile Pro")
                .email("contact@textilepro.ma")
                .ville("Casablanca")
                .ice("001234567890123")
                .build();

        fournisseurDTO = FournisseurDTO.builder()
                .id(1L)
                .societe("Textile Pro")
                .email("contact@textilepro.ma")
                .ville("Casablanca")
                .ice("001234567890123")
                .build();
    }

    @Test
    @DisplayName("Créer un fournisseur - Succès")
    void testCreateFournisseur_Success() {
        // Given
        when(fournisseurRepository.existsByEmail(fournisseurDTO.getEmail())).thenReturn(false);
        when(fournisseurRepository.existsByIce(fournisseurDTO.getIce())).thenReturn(false);
        when(fournisseurMapper.toEntity(fournisseurDTO)).thenReturn(fournisseur);
        when(fournisseurRepository.save(any(Fournisseur.class))).thenReturn(fournisseur);
        when(fournisseurMapper.toDTO(fournisseur)).thenReturn(fournisseurDTO);

        // When
        FournisseurDTO result = fournisseurService.create(fournisseurDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSociete()).isEqualTo("Textile Pro");
        verify(fournisseurRepository).save(any(Fournisseur.class));
    }

    @Test
    @DisplayName("Créer un fournisseur - Email déjà existant")
    void testCreateFournisseur_EmailAlreadyExists() {
        // Given
        when(fournisseurRepository.existsByEmail(fournisseurDTO.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> fournisseurService.create(fournisseurDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("email existe déjà");

        verify(fournisseurRepository, never()).save(any());
    }

    @Test
    @DisplayName("Créer un fournisseur - ICE déjà existant")
    void testCreateFournisseur_IceAlreadyExists() {
        // Given
        when(fournisseurRepository.existsByEmail(anyString())).thenReturn(false);
        when(fournisseurRepository.existsByIce(fournisseurDTO.getIce())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> fournisseurService.create(fournisseurDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ICE existe déjà");
    }

    @Test
    @DisplayName("Obtenir un fournisseur par ID - Succès")
    void testGetById_Success() {
        // Given
        when(fournisseurRepository.findById(1L)).thenReturn(Optional.of(fournisseur));
        when(fournisseurMapper.toDTO(fournisseur)).thenReturn(fournisseurDTO);

        // When
        FournisseurDTO result = fournisseurService.getById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSociete()).isEqualTo("Textile Pro");
    }

    @Test
    @DisplayName("Obtenir un fournisseur par ID - Non trouvé")
    void testGetById_NotFound() {
        // Given
        when(fournisseurRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> fournisseurService.getById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("non trouvé");
    }

    @Test
    @DisplayName("Lister tous les fournisseurs avec pagination")
    void testGetAll_WithPagination() {
        // Given
        List<Fournisseur> fournisseurs = Arrays.asList(fournisseur);
        Page<Fournisseur> page = new PageImpl<>(fournisseurs);
        Pageable pageable = PageRequest.of(0, 10);

        when(fournisseurRepository.findAll(pageable)).thenReturn(page);
        when(fournisseurMapper.toDTOList(anyList())).thenReturn(Arrays.asList(fournisseurDTO));

        // When
        PageResponse<FournisseurDTO> result = fournisseurService.getAll(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Supprimer un fournisseur - Succès")
    void testDelete_Success() {
        // Given
        when(fournisseurRepository.existsById(1L)).thenReturn(true);

        // When
        fournisseurService.delete(1L);

        // Then
        verify(fournisseurRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Supprimer un fournisseur - Non trouvé")
    void testDelete_NotFound() {
        // Given
        when(fournisseurRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> fournisseurService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("non trouvé");

        verify(fournisseurRepository, never()).deleteById(any());
    }
}

