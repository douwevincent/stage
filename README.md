# Stage

Application web de gestion et d'evaluation des stages (backend Spring Boot + frontend Vue).

## Fonctionnalites principales

- Gestion des referentiels: annees academiques, niveaux, specialites, parcours, entreprises, encadreurs.
- Gestion des etudiants, inscriptions et declarations de stage.
- Workflow de validation des stages: `EN_ATTENTE_VALIDATION` -> `VALIDE` / `REJETE`.
- Evaluation des stages via sessions d'evaluation et criteres de bareme.
- Evaluation publique securisee par code d'acces (sans compte applicatif).
- Authentification JWT et gestion des roles applicatifs.
- Notifications planifiees (creation en file d'attente + envoi).

## Stack technique

- Backend: Java 17, Spring Boot 4, Spring Data JPA, Spring Security, JWT, Quartz/Scheduling.
- Base de donnees: MySQL (profil par defaut), H2 en memoire (profil `dev`).
- Frontend: Vue 3, Vite, TypeScript, Pinia, Naive UI.
- Build: Maven (backend), npm (frontend).

## Structure du projet

- `src/main/java/cm/univ/maroua/enspm/stage`: code backend.
- `src/main/resources`: configuration Spring (`application.properties`, `application-dev.properties`), `data.sql`.
- `frontend`: application Vue.

## Prerequis

- JDK 17+
- Maven 3.9+ (ou wrapper `./mvnw`)
- Node.js 20+ (recommande) + npm
- MySQL 8+ (pour le profil par defaut)

## Configuration backend

### Profil par defaut (MySQL)

Le backend utilise `src/main/resources/application.properties`:

- URL: `jdbc:mysql://localhost:3306/stage2?createDatabaseIfNotExist=true`
- Utilisateur: `stage`
- Mot de passe: `_Stage123`

Variables d'environnement utiles:

- `APP_BASE_URL` (defaut: `http://localhost:8080`)
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION_MS`
- `SUPER_ADMIN_EMAIL`
- `SUPER_ADMIN_PASSWORD`

### Profil developpement (H2)

Le profil `dev` utilise H2 en memoire (`application-dev.properties`) et active la console H2.

## Lancer le backend

### Avec MySQL (profil par defaut)

```bash
./mvnw spring-boot:run
```

### Avec H2 (profil `dev`)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Application disponible sur `http://localhost:8080`.

## Lancer le frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend disponible via Vite (en general `http://localhost:5173`).

Le proxy Vite redirige les appels frontend `/api/*` vers le backend `/api/v1/*`.

## Build

### Backend

```bash
./mvnw clean package
```

### Frontend

```bash
cd frontend
npm run build
```

## API REST

- Base path backend: `/api/v1`
- Exemples de ressources: `/stages`, `/etudiants`, `/inscriptions`, `/sessions-evaluations`, `/auth`.

La documentation OpenAPI/Swagger est dependante de la configuration:

- Endpoints: `/v3/api-docs`, `/swagger-ui.html`
- Desactivee par defaut dans `application.properties`.

## Documentation Javadoc du backend

Une documentation Javadoc a ete ajoutee:

- Au niveau des packages backend (`package-info.java`): domaine, services, REST, securite, persistance.
- Sur les classes metier et techniques principales: application, entites coeur, services coeur, controleurs clefs.

Pour generer la Javadoc localement:

```bash
./mvnw javadoc:javadoc
```

Puis ouvrir le fichier genere dans `target/site/apidocs/index.html`.

## Securite

- Authentification via JWT Bearer token.
- Endpoint public de login: `POST /api/v1/auth/login`.
- Endpoint public de declaration de stage: `POST /api/v1/stages/declarer`.
- Endpoints publics d'evaluation: `/api/v1/public/evaluations/**`.

## Notes d'exploitation

- `spring.jpa.hibernate.ddl-auto=update` est active: le schema est mis a jour automatiquement.
- `data.sql` est utilise pour initialiser certaines donnees.
- Les fichiers d'autorisation de stage sont stockes dans le dossier configure par `app.upload.dir` (defaut: `uploads`).
