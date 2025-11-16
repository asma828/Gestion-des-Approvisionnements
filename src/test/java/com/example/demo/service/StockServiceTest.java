package com.example.demo.service;

import com.example.demo.entity.MouvementStock;
import com.example.demo.entity.Produit;
import com.example.demo.Enum.TypeMouvement;
import com.example.demo.repository.MouvementStockRepository;
import com.example.demo.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - StockService (CUMP)")
class StockServiceTest {

    @Mock
    private MouvementStockRepository mouvementRepository;

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private StockService stockService;

    private Produit produit;

    @BeforeEach
    void setUp() {
        produit = Produit.builder()
                .id(1L)
                .nom("Tissu Coton")
                .stockActuel(0)
                .coutMoyenPondere(null)
                .build();
    }

    @Test
    @DisplayName("Créer ENTREE - Stock vide - CUMP = prix entrée")
    void testCreerMouvementEntree_StockVide() {
        // Given
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(mouvementRepository.save(any(MouvementStock.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(produitRepository.save(any(Produit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        stockService.creerMouvementEntree(
                1L,
                100,
                new BigDecimal("50.00"),
                1L,
                "Test entrée"
        );

        // Then
        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository).save(produitCaptor.capture());

        Produit savedProduit = produitCaptor.getValue();
        assertThat(savedProduit.getStockActuel()).isEqualTo(100);
        assertThat(savedProduit.getCoutMoyenPondere()).isEqualByComparingTo(new BigDecimal("50.00"));

        // Vérifier que le mouvement est créé
        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        verify(mouvementRepository).save(mouvementCaptor.capture());

        MouvementStock savedMouvement = mouvementCaptor.getValue();
        assertThat(savedMouvement.getType()).isEqualTo(TypeMouvement.ENTREE);
        assertThat(savedMouvement.getQuantite()).isEqualTo(100);
        assertThat(savedMouvement.getCoutUnitaire()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("Créer ENTREE - Avec stock existant - CUMP recalculé")
    void testCreerMouvementEntree_AvecStockExistant() {
        // Given - Stock initial: 100 unités à 45 MAD
        produit.setStockActuel(100);
        produit.setCoutMoyenPondere(new BigDecimal("45.00"));

        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(mouvementRepository.save(any(MouvementStock.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(produitRepository.save(any(Produit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When - Nouvelle entrée: 50 unités à 51 MAD
        stockService.creerMouvementEntree(
                1L,
                50,
                new BigDecimal("51.00"),
                2L,
                "Test entrée 2"
        );

        // Then
        // Calcul CUMP attendu: (100*45 + 50*51) / 150 = (4500 + 2550) / 150 = 47.00
        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository).save(produitCaptor.capture());

        Produit savedProduit = produitCaptor.getValue();
        assertThat(savedProduit.getStockActuel()).isEqualTo(150);
        assertThat(savedProduit.getCoutMoyenPondere()).isEqualByComparingTo(new BigDecimal("47.00"));
    }

    @Test
    @DisplayName("Créer SORTIE - Stock suffisant - CUMP inchangé")
    void testCreerMouvementSortie_StockSuffisant() {
        // Given
        produit.setStockActuel(100);
        produit.setCoutMoyenPondere(new BigDecimal("45.00"));

        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(mouvementRepository.save(any(MouvementStock.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(produitRepository.save(any(Produit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        stockService.creerMouvementSortie(1L, 30, "PROD-001", "Production");

        // Then
        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository).save(produitCaptor.capture());

        Produit savedProduit = produitCaptor.getValue();
        assertThat(savedProduit.getStockActuel()).isEqualTo(70); // 100 - 30
        assertThat(savedProduit.getCoutMoyenPondere()).isEqualByComparingTo(new BigDecimal("45.00")); // Inchangé!

        // Vérifier le mouvement
        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        verify(mouvementRepository).save(mouvementCaptor.capture());

        MouvementStock savedMouvement = mouvementCaptor.getValue();
        assertThat(savedMouvement.getType()).isEqualTo(TypeMouvement.SORTIE);
        assertThat(savedMouvement.getQuantite()).isEqualTo(30);
    }

    @Test
    @DisplayName("Créer SORTIE - Stock insuffisant - Exception")
    void testCreerMouvementSortie_StockInsuffisant() {
        // Given
        produit.setStockActuel(10);
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));

        // When & Then
        assertThatThrownBy(() ->
                stockService.creerMouvementSortie(1L, 50, "REF", "Test")
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuffisant");

        verify(mouvementRepository, never()).save(any());
        verify(produitRepository, never()).save(any());
    }

    @Test
    @DisplayName("Créer AJUSTEMENT - Positif")
    void testCreerMouvementAjustement_Positif() {
        // Given
        produit.setStockActuel(50);
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(mouvementRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(produitRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When
        stockService.creerMouvementAjustement(1L, 10, "INV-001", "Inventaire");

        // Then
        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository).save(produitCaptor.capture());

        assertThat(produitCaptor.getValue().getStockActuel()).isEqualTo(60); // 50 + 10
    }

    @Test
    @DisplayName("Créer AJUSTEMENT - Négatif")
    void testCreerMouvementAjustement_Negatif() {
        // Given
        produit.setStockActuel(50);
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(mouvementRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(produitRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // When
        stockService.creerMouvementAjustement(1L, -5, "INV-001", "Correction");

        // Then
        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository).save(produitCaptor.capture());

        assertThat(produitCaptor.getValue().getStockActuel()).isEqualTo(45); // 50 - 5
    }

    @Test
    @DisplayName("Créer AJUSTEMENT - Stock négatif impossible")
    void testCreerMouvementAjustement_StockNegatifImpossible() {
        // Given
        produit.setStockActuel(10);
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));

        // When & Then
        assertThatThrownBy(() ->
                stockService.creerMouvementAjustement(1L, -20, "INV", "Test")
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ajustement impossible");
    }
}

