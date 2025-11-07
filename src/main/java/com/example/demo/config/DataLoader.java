package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final FournisseurRepository fournisseurRepository;
    private final ProduitRepository produitRepository;

    @Bean
    @Profile("dev")
    public CommandLineRunner loadData() {
        return args -> {
            log.info("Loading sample data...");

            // Create Fournisseurs
            if (fournisseurRepository.count() == 0) {
                Fournisseur f1 = Fournisseur.builder()
                        .societe("Textile Pro")
                        .adresse("123 Rue Mohammed V")
                        .contact("Ahmed Benali")
                        .email("contact@textilepro.ma")
                        .telephone("0522-123456")
                        .ville("Casablanca")
                        .ice("001234567890123")
                        .build();

                Fournisseur f2 = Fournisseur.builder()
                        .societe("Fabrics International")
                        .adresse("456 Boulevard Zerktouni")
                        .contact("Fatima Zahra")
                        .email("info@fabrics-int.ma")
                        .telephone("0522-654321")
                        .ville("Casablanca")
                        .ice("001234567890124")
                        .build();

                Fournisseur f3 = Fournisseur.builder()
                        .societe("Morocco Textiles")
                        .adresse("789 Avenue Hassan II")
                        .contact("Karim Alaoui")
                        .email("sales@morocco-textiles.ma")
                        .telephone("0537-111222")
                        .ville("Rabat")
                        .ice("001234567890125")
                        .build();

                fournisseurRepository.saveAll(Arrays.asList(f1, f2, f3));
                log.info("Created 3 fournisseurs");
            }

            // Create Produits
            if (produitRepository.count() == 0) {
                Produit p1 = Produit.builder()
                        .nom("Tissu Coton Blanc")
                        .description("Tissu 100% coton, haute qualité")
                        .prixUnitaire(new BigDecimal("45.50"))
                        .categorie("Tissu")
                        .stockActuel(0)
                        .build();

                Produit p2 = Produit.builder()
                        .nom("Boutons Plastique")
                        .description("Lot de 100 boutons blancs")
                        .prixUnitaire(new BigDecimal("12.00"))
                        .categorie("Accessoire")
                        .stockActuel(0)
                        .build();

                Produit p3 = Produit.builder()
                        .nom("Fil à Coudre Polyester")
                        .description("Bobine 1000m, couleurs assorties")
                        .prixUnitaire(new BigDecimal("8.50"))
                        .categorie("Consommable")
                        .stockActuel(0)
                        .build();

                Produit p4 = Produit.builder()
                        .nom("Fermetures Éclair 50cm")
                        .description("Fermeture éclair métallique")
                        .prixUnitaire(new BigDecimal("3.75"))
                        .categorie("Accessoire")
                        .stockActuel(0)
                        .build();

                Produit p5 = Produit.builder()
                        .nom("Tissu Polyester Bleu")
                        .description("Tissu polyester résistant")
                        .prixUnitaire(new BigDecimal("35.00"))
                        .categorie("Tissu")
                        .stockActuel(0)
                        .build();

                produitRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));
                log.info("Created 5 produits");
            }

            log.info("Sample data loaded successfully!");
            log.info("You can now test the API endpoints");
        };
    }
}
