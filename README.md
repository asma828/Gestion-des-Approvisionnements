# Tricol - Supplier Order Management System

API REST complète pour la gestion des commandes fournisseurs avec Spring Boot.


##  Technologies

- **Spring Boot 3.5.7**
- **Spring Data JPA** - Persistence
- **MapStruct 1.5.5** - Entity ↔ DTO mapping
- **Liquibase** - Database migrations
- **MySQL** - Base de données
- **Swagger/OpenAPI** - Documentation API
- **Lombok** - Reduce boilerplate code
- **Jakarta Validation** - Data validation

##  Architecture

```
com.tricol/
├── config/          # Configuration classes (Swagger, CORS, DataLoader)
├── controller/      # REST Controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA Entities
├── exception/       # Exception handling
├── mapper/          # MapStruct Mappers
├── repository/      # Spring Data JPA Repositories
└── service/         # Business Logic Layer
```

##  Installation

### Prérequis
- Java 17 ou supérieur
- Maven 3.6+
- MySQL 8.0+

### Étapes d'installation

1. **Cloner le projet**
```bash
git clone <https://github.com/asma828/Gestion-des-Approvisionnements.git>
cd tricol-supplier-management
```

2. **Créer la base de données MySQL**
```sql
CREATE DATABASE approvisionnements CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **Configurer application.properties**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/approvisionnements
spring.datasource.username=root
spring.datasource.password=your_password
```

4. **Compiler et exécuter**
```bash
mvn clean install
mvn spring-boot:run
```

L'application démarrera sur `http://localhost:8081`

##  Configuration

### Pagination par Défaut

```properties
app.pagination.default-page-size=10
app.pagination.max-page-size=100
```

##  API Endpoints

###  Fournisseurs

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/fournisseurs` | Créer un fournisseur |
| PUT | `/api/fournisseurs/{id}` | Modifier un fournisseur |
| GET | `/api/fournisseurs/{id}` | Obtenir un fournisseur |
| GET | `/api/fournisseurs` | Liste paginée |
| GET | `/api/fournisseurs/search?query=` | Rechercher |
| DELETE | `/api/fournisseurs/{id}` | Supprimer |

###  Produits

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/produits` | Créer un produit |
| PUT | `/api/produits/{id}` | Modifier un produit |
| GET | `/api/produits/{id}` | Obtenir un produit |
| GET | `/api/produits` | Liste paginée |
| GET | `/api/produits/search?query=` | Rechercher |
| DELETE | `/api/produits/{id}` | Supprimer |

###  Commandes Fournisseurs

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/commandes` | Créer une commande |
| PUT | `/api/commandes/{id}` | Modifier une commande |
| PATCH | `/api/commandes/{id}/valider` | Valider |
| PATCH | `/api/commandes/{id}/livrer` | Marquer livrée |
| PATCH | `/api/commandes/{id}/annuler` | Annuler |
| GET | `/api/commandes/{id}` | Obtenir une commande |
| GET | `/api/commandes` | Liste paginée |
| GET | `/api/commandes/fournisseur/{id}` | Par fournisseur |
| GET | `/api/commandes/statut/{statut}` | Par statut |
| DELETE | `/api/commandes/{id}` | Supprimer |

###  Mouvements de Stock

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/mouvements-stock` | Liste paginée |
| GET | `/api/mouvements-stock/{id}` | Obtenir un mouvement |
| GET | `/api/mouvements-stock/produit/{id}` | Par produit |
| GET | `/api/mouvements-stock/type/{type}` | Par type |
| GET | `/api/mouvements-stock/commande/{id}` | Par commande |
##  Test de l'API

### Accéder à Swagger UI
```
http://localhost:8081/swagger-ui.html
```




##  Fonctionnalités Clés

### 1. Gestion du Cycle de Vie des Commandes
- **EN_ATTENTE** → **VALIDÉE** → **LIVRÉE**
- Possibilité d'annulation (ANNULÉE)
- Transitions d'état contrôlées

### 2. Valorisation du Stock

#### CUMP (Coût Unitaire Moyen Pondéré)
```
Nouveau CUMP = (Stock Ancien × CUMP Ancien + Quantité Entrée × Prix Entrée) 
               / (Stock Ancien + Quantité Entrée)
```

### 3. Mouvements de Stock Automatiques
Lorsqu'une commande est livrée:
- Création automatique de mouvements d'ENTRÉE
- Mise à jour du stock actuel
- Recalcul du coût moyen (si CUMP)

### 4. Pagination et Filtrage
Tous les endpoints de listing supportent:
- `page`: numéro de page (défaut: 0)
- `size`: taille de page (défaut: 10)
- `sortBy`: champ de tri
- `sortDir`: direction (ASC/DESC)

### 5. Validation des Données
- Jakarta Validation sur tous les DTOs
- Messages d'erreur personnalisés en français
- Gestion globale des exceptions

### 6. Recherche Avancée
- Recherche textuelle sur fournisseurs et produits
- Filtrage par catégorie, statut, date
- Recherche insensible à la casse

##  Modèle de Données

### Entités Principales

1. **Fournisseur** - Informations fournisseur
2. **Produit** - Produits commandables
3. **CommandeFournisseur** - Commandes
4. **LigneCommande** - Détails commande (produit + quantité)
5. **MouvementStock** - Historique des mouvements

### Relations
- Fournisseur 1-N Commandes
- Commande 1-N LignesCommande
- Commande N-M Produits
- Produit 1-N Mouvements
- Commande 1-N Mouvements

##  Liquibase Migrations

Les scripts de migration sont dans `src/main/resources/db/changelog/changes/`:
- `001-create-fournisseurs-table.xml`
- `002-create-produits-table.xml`
- `003-create-commandes-fournisseurs-table.xml`
- `004-create-lignes-commande-table.xml`
- `005-create-mouvements-stock-table.xml`
- `006-create-commande-produit-table.xml`


## Planification des taches

- Lien Jira : https://asmalachhab.atlassian.net/jira/software/projects/GDA/boards/509?sprintStarted=true

---

**Développé pour Tricol** - Système de Gestion des Commandes Fournisseurs