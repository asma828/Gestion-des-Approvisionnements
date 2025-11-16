package com.example.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.dto.CreateCommandeRequest;
import com.example.demo.dto.LigneCommandeDTO;
import com.example.demo.entity.*;
import com.example.demo.Enum.StatutCommande;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'Intégration - CommandeFournisseurController")
class CommandeFournisseurControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommandeFournisseurRepository commandeRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private MouvementStockRepository mouvementRepository;

    private Fournisseur fournisseur;
    private Produit produit;

    @BeforeEach
    void setUp() {
        mouvementRepository.deleteAll();
        commandeRepository.deleteAll();
        produitRepository.deleteAll();
        fournisseurRepository.deleteAll();

        fournisseur = fournisseurRepository.save(
                Fournisseur.builder()
                        .societe("Test Supplier")
                        .email("supplier@test.com")
                        .build()
        );

        produit = produitRepository.save(
                Produit.builder()
                        .nom("Test Product")
                        .prixUnitaire(new BigDecimal("50.00"))
                        .categorie("Tissu")
                        .stockActuel(0)
                        .build()
        );
    }

    @Test
    @DisplayName("POST /api/commandes - Créer une commande avec mouvement ENTREE")
    void testCreateCommande_CreatesEntreeMovement() throws Exception {
        // Given
        CreateCommandeRequest request = CreateCommandeRequest.builder()
                .dateCommande(LocalDate.now())
                .fournisseurId(fournisseur.getId())
                .lignesCommande(Arrays.asList(
                        LigneCommandeDTO.builder()
                                .produitId(produit.getId())
                                .quantite(100)
                                .prixUnitaire(new BigDecimal("50.00"))
                                .build()
                ))
                .build();

        // When & Then
        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"))
                .andExpect(jsonPath("$.montantTotal").value(5000.00));

        // Vérifier que le stock a été mis à jour (ENTREE)
        Produit updatedProduit = produitRepository.findById(produit.getId()).get();
        assert updatedProduit.getStockActuel() == 100;
        assert updatedProduit.getCoutMoyenPondere().compareTo(new BigDecimal("50.00")) == 0;
    }

    @Test
    @DisplayName("PATCH /api/commandes/{id}/valider - Valider une commande")
    void testValiderCommande() throws Exception {
        // Given
        CommandeFournisseur commande = createTestCommande();

        // When & Then
        mockMvc.perform(patch("/api/commandes/" + commande.getId() + "/valider"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("VALIDEE"));
    }

    @Test
    @DisplayName("PATCH /api/commandes/{id}/livrer - Livrer une commande avec mouvement SORTIE")
    void testLivrerCommande_CreatesSortieMovement() throws Exception {
        // Given
        CommandeFournisseur commande = createTestCommande();
        commande.setStatut(StatutCommande.VALIDEE);
        commandeRepository.save(commande);

        // Stock initial après création
        Produit produitAvant = produitRepository.findById(produit.getId()).get();
        int stockAvant = produitAvant.getStockActuel();

        // When & Then
        mockMvc.perform(patch("/api/commandes/" + commande.getId() + "/livrer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("LIVREE"));

        // Vérifier que le stock a diminué (SORTIE)
        Produit produitApres = produitRepository.findById(produit.getId()).get();
        assert produitApres.getStockActuel() < stockAvant;
    }

    @Test
    @DisplayName("GET /api/commandes/{id} - Obtenir détails d'une commande")
    void testGetCommandeById() throws Exception {
        // Given
        CommandeFournisseur commande = createTestCommande();

        // When & Then
        mockMvc.perform(get("/api/commandes/" + commande.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commande.getId()))
                .andExpect(jsonPath("$.lignesCommande", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("GET /api/commandes/statut/{statut} - Filtrer par statut")
    void testGetCommandesByStatut() throws Exception {
        // Given
        createTestCommande();

        // When & Then
        mockMvc.perform(get("/api/commandes/statut/EN_ATTENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].statut").value("EN_ATTENTE"));
    }

    @Test
    @DisplayName("PATCH /api/commandes/{id}/annuler - Annuler une commande")
    void testAnnulerCommande() throws Exception {
        // Given
        CommandeFournisseur commande = createTestCommande();

        // When & Then
        mockMvc.perform(patch("/api/commandes/" + commande.getId() + "/annuler"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("ANNULEE"));
    }

    private CommandeFournisseur createTestCommande() {
        LigneCommande ligne = LigneCommande.builder()
                .produit(produit)
                .quantite(50)
                .prixUnitaire(new BigDecimal("50.00"))
                .sousTotal(new BigDecimal("2500.00"))
                .build();

        CommandeFournisseur commande = CommandeFournisseur.builder()
                .dateCommande(LocalDate.now())
                .statut(StatutCommande.EN_ATTENTE)
                .fournisseur(fournisseur)
                .montantTotal(new BigDecimal("2500.00"))
                .build();

        ligne.setCommande(commande);
        commande.getLignesCommande().add(ligne);

        return commandeRepository.save(commande);
    }
}
