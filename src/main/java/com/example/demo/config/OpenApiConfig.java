package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tricolOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8081");
        devServer.setDescription("Serveur de développement");

        Contact contact = new Contact();
        contact.setName("Tricol Support");
        contact.setEmail("support@tricol.ma");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Tricol - API Gestion des Commandes Fournisseurs")
                .version("1.0.0")
                .description("API REST pour la gestion des commandes fournisseurs, produits et mouvements de stock")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
