package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.Enum.StatutCommande;
import com.example.demo.mapper.CommandeFournisseurMapper;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - CommandeFournisseurService")
class CommandeFournisseurServiceTest {

    @Mock
    private CommandeFournisseurRepository commandeRepository;

    @Mock
    private FournisseurRepository fournisseurRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private StockService stockService;

    @Mock
    private CommandeFournisseurMapper commandeMapper;

    @InjectMocks
    private CommandeFournisseurService commandeService;

    private Fournisseur fournisseur;
    private Produit produit;
    private CommandeFournisseur commande;
    private CommandeFournisseurDTO commandeDTO;
    private CreateCommandeRequest request;

    @BeforeEach
    void setUp() {
        fournisseur = Fournisseur.builder()
                .id(1L)
                .societe("Textile Pro")
                .build();

        produit = Produit.builder()
                .id(1L)
                .nom("Tissu Coton")
                .prixUnitaire(new BigDecimal("45.50"))
                .stockActuel(0)
                .build();

        commande = CommandeFournisseur.builder()
                .id(1L)
                .dateCommande(LocalDate.now())
                .statut(StatutCommande.EN_ATTENTE)
                .fournisseur(fournisseur)
                .lignesCommande(new ArrayList<>())
                .build();

        commandeDTO = CommandeFournisseurDTO.builder()
                .id(1L)
                .statut(StatutCommande.EN_ATTENTE)
                .build();

        LigneCommandeDTO ligneDTO = LigneCommandeDTO.builder()
                .produitId(1L)
                .quantite(100)
                .prixUnitaire(new BigDecimal("45.50"))
                .build();

        request = CreateCommandeRequest.builder()
                .dateCommande(LocalDate.now())
                .fournisseurId(1L)
                .lignesCommande(Arrays.asList(ligneDTO))
                .build();
    }

    @Test
    @DisplayName("Créer commande - Succès avec mouvement ENTREE")
    void testCreateCommande_Success() {
        // Given
        when(fournisseurRepository.findById(1L)).thenReturn(Optional.of(fournisseur));
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(commandeRepository.save(any(CommandeFournisseur.class))).thenReturn(commande);
        when(commandeMapper.toDTO(any())).thenReturn(commandeDTO);
        doNothing().when(stockService).creerMouvementEntree(anyLong(), any(), any(), anyLong(), any());

        // When
        CommandeFournisseurDTO result = commandeService.create(request);

        // Then
        assertThat(result).isNotNull();
        verify(commandeRepository).save(any(CommandeFournisseur.class));
        verify(stockService).creerMouvementEntree(eq(1L), eq(100), any(), anyLong(), any());
    }

    @Test
    @DisplayName("Créer commande - Fournisseur non trouvé")
    void testCreateCommande_FournisseurNotFound() {
        // Given
        when(fournisseurRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        CreateCommandeRequest invalidRequest = CreateCommandeRequest.builder()
                .fournisseurId(999L)
                .build();

        assertThatThrownBy(() -> commandeService.create(invalidRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Fournisseur non trouvé");
    }

    @Test
    @DisplayName("Valider commande - Succès")
    void testValiderCommande_Success() {
        // Given
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any())).thenReturn(commande);
        when(commandeMapper.toDTO(any())).thenReturn(commandeDTO);

        // When
        CommandeFournisseurDTO result = commandeService.validerCommande(1L);

        // Then
        assertThat(result).isNotNull();
        verify(commandeRepository).save(any());
    }

    @Test
    @DisplayName("Valider commande - Statut incorrect")
    void testValiderCommande_StatutIncorrect() {
        // Given
        commande.setStatut(StatutCommande.LIVREE);
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));

        // When & Then
        assertThatThrownBy(() -> commandeService.validerCommande(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("en attente");
    }

    @Test
    @DisplayName("Livrer commande - Succès avec mouvement SORTIE")
    void testLivrerCommande_Success() {
        // Given
        commande.setStatut(StatutCommande.VALIDEE);
        LigneCommande ligne = LigneCommande.builder()
                .produit(produit)
                .quantite(100)
                .prixUnitaire(new BigDecimal("45.50"))
                .build();
        commande.getLignesCommande().add(ligne);

        when(commandeRepository.findByIdWithDetails(1L)).thenReturn(commande);
        when(commandeRepository.save(any())).thenReturn(commande);
        when(commandeMapper.toDTO(any())).thenReturn(commandeDTO);
        doNothing().when(stockService).creerMouvementSortie(anyLong(), any(), any(), any());

        // When
        CommandeFournisseurDTO result = commandeService.livrerCommande(1L);

        // Then
        assertThat(result).isNotNull();
        verify(stockService).creerMouvementSortie(eq(1L), eq(100), any(), any());
    }

    @Test
    @DisplayName("Livrer commande - Statut doit être VALIDEE")
    void testLivrerCommande_StatutNonValidee() {
        // Given
        commande.setStatut(StatutCommande.EN_ATTENTE);
        when(commandeRepository.findByIdWithDetails(1L)).thenReturn(commande);

        // When & Then
        assertThatThrownBy(() -> commandeService.livrerCommande(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("validées");
    }

    @Test
    @DisplayName("Annuler commande - Succès")
    void testAnnulerCommande_Success() {
        // Given
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any())).thenReturn(commande);
        when(commandeMapper.toDTO(any())).thenReturn(commandeDTO);

        // When
        CommandeFournisseurDTO result = commandeService.annulerCommande(1L);

        // Then
        assertThat(result).isNotNull();
        verify(commandeRepository).save(any());
    }

    @Test
    @DisplayName("Annuler commande - Impossible si déjà livrée")
    void testAnnulerCommande_DejaLivree() {
        // Given
        commande.setStatut(StatutCommande.LIVREE);
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));

        // When & Then
        assertThatThrownBy(() -> commandeService.annulerCommande(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("livrée");
    }
}
