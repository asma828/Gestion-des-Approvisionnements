package com.example.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.dto.FournisseurDTO;
import com.example.demo.entity.Fournisseur;
import com.example.demo.repository.FournisseurRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'Intégration - FournisseurController")
class FournisseurControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @BeforeEach
    void setUp() {
        fournisseurRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/fournisseurs - Créer un fournisseur")
    void testCreateFournisseur() throws Exception {
        // Given
        FournisseurDTO dto = FournisseurDTO.builder()
                .societe("Textile Pro Test")
                .email("test@textilepro.ma")
                .ville("Casablanca")
                .ice("001234567890123")
                .telephone("0522-123456")
                .build();

        // When & Then
        mockMvc.perform(post("/api/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.societe").value("Textile Pro Test"))
                .andExpect(jsonPath("$.email").value("test@textilepro.ma"))
                .andExpect(jsonPath("$.ville").value("Casablanca"));
    }

    @Test
    @DisplayName("POST /api/fournisseurs - Validation échoue si données manquantes")
    void testCreateFournisseur_ValidationFails() throws Exception {
        // Given - Sans société (obligatoire)
        FournisseurDTO dto = FournisseurDTO.builder()
                .email("test@test.com")
                .build();

        // When & Then
        mockMvc.perform(post("/api/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/fournisseurs/{id} - Obtenir un fournisseur")
    void testGetFournisseurById() throws Exception {
        // Given
        Fournisseur fournisseur = Fournisseur.builder()
                .societe("Test Company")
                .email("test@test.com")
                .build();
        fournisseur = fournisseurRepository.save(fournisseur);

        // When & Then
        mockMvc.perform(get("/api/fournisseurs/" + fournisseur.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fournisseur.getId()))
                .andExpect(jsonPath("$.societe").value("Test Company"));
    }

    @Test
    @DisplayName("GET /api/fournisseurs - Liste avec pagination")
    void testGetAllFournisseurs_WithPagination() throws Exception {
        // Given
        fournisseurRepository.save(Fournisseur.builder().societe("F1").email("f1@test.com").build());
        fournisseurRepository.save(Fournisseur.builder().societe("F2").email("f2@test.com").build());
        fournisseurRepository.save(Fournisseur.builder().societe("F3").email("f3@test.com").build());

        // When & Then
        mockMvc.perform(get("/api/fournisseurs")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    @DisplayName("PUT /api/fournisseurs/{id} - Modifier un fournisseur")
    void testUpdateFournisseur() throws Exception {
        // Given
        Fournisseur fournisseur = fournisseurRepository.save(
                Fournisseur.builder().societe("Old Name").email("old@test.com").build()
        );

        FournisseurDTO updateDto = FournisseurDTO.builder()
                .societe("New Name")
                .email("new@test.com")
                .build();

        // When & Then
        mockMvc.perform(put("/api/fournisseurs/" + fournisseur.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.societe").value("New Name"));
    }

    @Test
    @DisplayName("DELETE /api/fournisseurs/{id} - Supprimer un fournisseur")
    void testDeleteFournisseur() throws Exception {
        // Given
        Fournisseur fournisseur = fournisseurRepository.save(
                Fournisseur.builder().societe("To Delete").email("delete@test.com").build()
        );

        // When & Then
        mockMvc.perform(delete("/api/fournisseurs/" + fournisseur.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/fournisseurs/" + fournisseur.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/fournisseurs/search - Rechercher des fournisseurs")
    void testSearchFournisseurs() throws Exception {
        // Given
        fournisseurRepository.save(Fournisseur.builder().societe("Textile Pro").email("tp@test.com").build());
        fournisseurRepository.save(Fournisseur.builder().societe("Fabrics Int").email("fi@test.com").build());

        // When & Then
        mockMvc.perform(get("/api/fournisseurs/search")
                        .param("query", "Textile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].societe").value(containsString("Textile")));
    }
}
