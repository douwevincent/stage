# Manuel Utilisateur Frontend (Version Courte)

## Page de garde

- Application: Stage Manager ENSPM
- Document: Manuel Utilisateur Frontend (version courte)
- Version: 1.0
- Date d'édition: 15 avril 2026
- Statut: guide opérationnel rapide
- Public cible: utilisateurs métier (scolarité, administration)
- Usage: support quotidien imprimable (lecture rapide)
- Auteur: Équipe projet Stage Manager ENSPM
- Validateur fonctionnel: à compléter
- Validateur métier: à compléter

### Historique d'approbation

| Date | Validateur | Rôle | Statut | Commentaire |
| --- | --- | --- | --- | --- |
| 15/04/2026 | À compléter | Fonctionnel | En attente | Validation initiale du guide court |
| 15/04/2026 | À compléter | Métier | En attente | Validation de l'usage quotidien |

---

## 0. Captures essentielles

Deposez les images dans `docs/images/manuel-court/`.
Convention de nommage: voir `docs/images/README.md`.
Ordre de prise recommande: [docs/images/checklist-captures.md](images/checklist-captures.md)

### 0.1 Connexion
![Connexion](images/manuel-court/01-login.png)

### 0.2 Navigation principale
![Navigation principale](images/manuel-court/02-navigation.png)

### 0.3 Inscriptions
![Inscriptions](images/manuel-court/03-inscriptions.png)

### 0.4 Stages
![Stages](images/manuel-court/04-stages.png)

### 0.5 Evaluations
![Evaluations](images/manuel-court/05-evaluations.png)

### 0.6 Systeme
![Systeme](images/manuel-court/06-systeme.png)

## 1. Démarrage rapide

### 1.1 Se connecter
1. Ouvrir la page Login.
2. Saisir email et mot de passe.
3. Cliquer sur Se connecter.

Résultat attendu:
- Ouverture du Tableau de Bord.

### 1.2 Se déconnecter
1. Cliquer sur l'icône de déconnexion en haut à droite.

Résultat attendu:
- Retour à l'écran de connexion.

## 2. Navigation

Les menus principaux:
- Tableau de Bord
- Structure académique
- Gestion étudiants
- Gestion stages
- Évaluation
- Système

Comportements communs:
- Ajouter: crée un nouvel élément.
- Éditer: modifie une ligne.
- Supprimer: retire une ligne (avec confirmation).
- Filtres + recherche: restreignent la liste.
- Pagination: change de page et de taille (10/20/50).

## 3. Parcours métier essentiels

### 3.1 Préparer l'année académique
Objectif: mettre en place le référentiel avant opérations.

Ordre recommandé:
1. Années Académiques: créer/activer l'année.
2. Niveaux, Départements, Spécialités: compléter les référentiels.
3. Parcours: créer les combinaisons nécessaires.
4. Types de stage et Périodes de stage: cadrer les dates.

### 3.2 Inscrire un étudiant
Objectif: rattacher l'étudiant à un parcours.

1. Ouvrir Gestion étudiants > Inscriptions.
2. Cliquer sur Ajouter une inscription.
3. Sélectionner Étudiant, Département, Niveau, Spécialité.
4. Cliquer sur Enregistrer.

Contrôles importants:
- La spécialité est activée seulement après département + niveau.
- Les doublons sont bloqués automatiquement.

### 3.3 Créer et traiter un stage
Objectif: enregistrer un stage puis le qualifier.

1. Ouvrir Gestion stages > Stages.
2. Cliquer sur Nouveau Stage.
3. Saisir type, entreprise, lieu, dates.
4. Enregistrer.
5. Si besoin: Assigner étudiant et Assigner encadreur.
6. Traiter le statut (Valider/Rejeter) selon le workflow.

### 3.4 Produire les résultats d'évaluation
Objectif: consulter et exporter les résultats.

1. Ouvrir Évaluation > Résultats des évaluations.
2. Appliquer les filtres (département, niveau, spécialité).
3. Ouvrir Details pour analyser une session.
4. Cliquer sur Fiche PDF pour un export unitaire.
5. Aller à Exporter les evaluations pour export global (PDF/Excel).

## 4. Module Système (admin)

### 4.1 Notifications
- Créer des règles de notification par type de stage.
- Définir référence temporelle et décalage en jours.
- Activer/désactiver les règles.

### 4.2 Paramètres
- Modifier les clés marquées modifiables.
- Vérifier la description avant enregistrement.
- Les clés non modifiables restent verrouillées.

### 4.3 Mails envoyés
- Rechercher par destinataire/sujet/erreur.
- Filtrer par statut (En attente, Envoyé, Échec).
- Relancer les échecs (unitaire ou en lot).
- Purger les mails anciens selon un seuil en jours.

### 4.4 Utilisateurs (SUPER_ADMIN)
- Créer des comptes et attribuer des rôles.
- Activer/désactiver un compte.
- Réinitialiser mot de passe.
- Supprimer un compte (hors compte connecté).

## 5. Parcours public encadreur

### 5.1 Accéder au lien public
1. Ouvrir le lien reçu (avec code).
2. Choisir le stage à évaluer.
3. Cliquer sur Evaluer.

### 5.2 Soumettre une évaluation
1. Renseigner chaque note par critère.
2. Respecter 0 <= note <= coefficient.
3. Ajouter un commentaire optionnel.
4. Cliquer sur Soumettre l'evaluation.

Résultat attendu:
- Confirmation d'enregistrement.

## 6. Dépannage rapide

### 6.1 Menu manquant
Cause: droits insuffisants.
Action: demander vérification du rôle.

### 6.2 Inscriptions: spécialité indisponible
Cause: département/niveau non sélectionnés.
Action: choisir d'abord département puis niveau.

### 6.3 Import Excel échoue
Cause: format invalide ou données incorrectes.
Action: corriger le fichier puis relancer.

### 6.4 Redirection vers Tableau de Bord
Cause: accès non autorisé.
Action: vérifier vos permissions.

### 6.5 Évaluation publique bloquée
Cause: lien invalide, expiré ou déjà utilisé.
Action: demander un nouveau lien.

## 7. Checklist rapide avant clôture quotidienne

1. Vérifier les stages en attente de validation.
2. Vérifier les inscriptions créées/modifiées du jour.
3. Vérifier les évaluations terminées et exports nécessaires.
4. Vérifier les mails en échec et relancer si besoin.
5. Signaler les anomalies de droits ou données à l'administrateur.

## 8. Références documentaires
- Manuel complet: docs/manuel-utilisateur-frontend.md
- Ce guide court: docs/manuel-utilisateur-frontend-court.md
