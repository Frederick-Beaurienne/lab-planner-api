# Laboratoire - Système de Planification

## Sommaire

* [Description](#description)
* [Architecture](#architecture)
* [Installation](#installation)
* [Utilisation](#utilisation)
* [Exemple de sortie JSON](#exemple-de-sortie-json)
* [Évolution depuis version SIMPLE](#évolution-depuis-version-simple)
* [Fonctionnalités actuellement supportées](#fonctionnalités-actuellement-supportées)
* [Justification des choix algorithmiques](#justification-des-choix-algorithmiques)
* [Validation des données](#validation-des-données)
* [Compatibilité et évolutivité](#compatibilité-et-évolutivité)
* [Gestion actuelle des pauses déjeuner](#gestion-actuelle-des-pauses-déjeuner)
* [Calcul de l’efficacité (efficiency)](#calcul-de-lefficacité-efficiency)
* [Limites et pistes d’amélioration](#limites-et-pistes-damélioration)
* [Tests](#tests)

---

# Description

Ce projet implémente un système de planification pour un laboratoire médical.

L’objectif est de générer automatiquement un planning d’analyses à partir :

* d’une liste d’échantillons,
* des techniciens disponibles,
* des équipements du laboratoire.

Le système respecte plusieurs contraintes métier :

* priorité des analyses (`STAT > URGENT > ROUTINE`),
* compatibilité technicien / échantillon,
* compatibilité équipement / échantillon,
* disponibilité des ressources,
* absence de conflits de planning.

Le système génère :

* un planning chronologique des analyses,
* des métriques de performance :

  * durée totale,
  * efficacité,
  * temps d’attente moyen,
  * taux de parallélisation,
  * conflits détectés.

---

# Architecture

Le projet suit une architecture Spring Boot classique :

* `controller` : exposition REST API,
* `service` : logique métier et moteur de planification,
* `model` : objets métier et DTO,
* `config` : paramètres globaux du laboratoire,
* `metrics` : calcul des indicateurs de performance,
* `scheduling` : gestion des timelines et réservations des ressources.

Le moteur de planification repose principalement sur :

* un système de timelines (`ResourceTimeline`),
* une gestion des réservations de ressources,
* une stratégie de planification greedy,
* une architecture pensée pour l’évolution progressive du moteur.

---

# Installation

## Cloner le projet

```bash
git clone https://github.com/Frederick-Beaurienne/lab-planner-api.git
```

Puis accéder au dossier du projet :

```bash
cd lab-planner-api
```

## Prérequis

* Java 17+
* Maven 3+

## Lancer le projet

```bash
./mvnw spring-boot:run
```

L’API sera accessible sur :

```text
http://localhost:8080
```

---

# Swagger

Documentation Swagger disponible sur :

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Utilisation

## Endpoint principal

```http
POST /api/planning
```

## Exemple de requête

```json
{
  "samples": [
    {
      "id": "S001",
      "type": "BLOOD",
      "priority": "STAT",
      "analysisTime": 45,
      "arrivalTime": "09:00",
      "patientId": "P001"
    },
    {
      "id": "S002",
      "type": "URINE",
      "priority": "ROUTINE",
      "analysisTime": 30,
      "arrivalTime": "09:15",
      "patientId": "P002"
    }
  ],
  "technicians": [
    {
      "id": "T001",
      "name": "Alice",
      "speciality": "BLOOD",
      "startTime": "08:00",
      "endTime": "17:00"
    },
    {
      "id": "T002",
      "name": "Bob",
      "speciality": "GENERAL",
      "startTime": "08:00",
      "endTime": "17:00"
    }
  ],
  "equipment": [
    {
      "id": "E001",
      "name": "Blood Analyzer",
      "type": "BLOOD",
      "available": true
    },
    {
      "id": "E002",
      "type": "URINE",
      "available": true
    }
  ]
}
```

---

# Exemple de sortie JSON

```json
{
  "metadata": {
    "lunchBreaks": [],
    "equipmentCleaning": []
  },
  "metrics": {
    "totalTime": 75,
    "efficiency": 100.0,
    "averageWaitTime": 30,
    "parallelismRate": 1.0,
    "conflicts": 0,
    "technicianUtilization": 13.89,
    "lunchInterruptions": 0
  },
  "schedule": [
    {
      "id": "S002",
      "technicianId": "T001",
      "equipmentId": "E001",
      "startTime": "09:30:00",
      "endTime": "10:00:00",
      "duration": 30,
      "priority": "STAT",
      "efficiency": 1.0
    },
    {
      "id": "S001",
      "technicianId": "T001",
      "equipmentId": "E001",
      "startTime": "10:00:00",
      "endTime": "10:45:00",
      "duration": 45,
      "priority": "URGENT",
      "efficiency": 1.0
    }
  ]
}
```

---

# Tester l’API avec Swagger

Une fois l’application lancée, la documentation interactive Swagger est accessible sur :

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger permet :

* de visualiser les endpoints disponibles,
* de consulter les modèles JSON,
* de tester directement les requêtes depuis le navigateur.

---

# Tester l’API avec Insomnia

Exemple de requête :

```http
POST http://localhost:8080/api/planning
```

Configurer :

* méthode : `POST`
* body : `JSON`

Puis envoyer une requête contenant les données du laboratoire.

Une collection Insomnia est fournie dans le dossier `/insomnia`
afin de faciliter les tests de l’API.

---

# Évolution depuis version SIMPLE

Le projet suit volontairement une architecture Spring Boot simple et lisible afin de rester cohérent avec le périmètre demandé.

Améliorations ajoutées :

* API REST Spring Boot,
* documentation Swagger/OpenAPI,
* séparation Controller / Service / Model,
* tri des échantillons par priorité puis heure d’arrivée,
* détection automatique des conflits de ressources,
* gestion des timelines de réservation,
* gestion des pauses déjeuner,
* gestion du nettoyage des équipements,
* calcul des métriques du planning.

---

# Fonctionnalités actuellement supportées

Le moteur de planification prend actuellement en charge :

- tri des analyses par priorité (`STAT > URGENT > ROUTINE`),
- prise en compte de l’heure d’arrivée des échantillons,
- compatibilité technicien / analyse,
- compatibilité équipement / analyse,
- gestion des horaires de travail des techniciens,
- gestion des pauses déjeuner,
- gestion du nettoyage des équipements,
- prévention des conflits de ressources,
- parallélisation des analyses lorsque possible,
- génération automatique d’un planning chronologique,
- calcul des métriques principales du planning :
  - durée totale,
  - efficacité,
  - temps d’attente moyen,
  - taux de parallélisation,
  - utilisation des techniciens,
  - interruptions de pause déjeuner,
  - conflits détectés,
- rétrocompatibilité partielle V1 / V2 des structures d’entrée.

---

# Validation des données

La validation des données d’entrée a été volontairement alignée sur les exemples fournis dans l’énoncé.

Certains champs descriptifs comme `name` ont été considérés comme optionnels lorsque les exemples de données ne les renseignaient pas systématiquement.

Les attributs d’entrée non pertinents pour les décisions de planification sont actuellement ignorés par le moteur de scheduling.

---

# Compatibilité et évolutivité

Certaines structures de données liées à la version INTERMEDIATE
(métadonnées, métriques avancées, etc.) sont déjà présentes dans le modèle afin de garantir l’évolutivité et la compatibilité avec les futures extensions du moteur de planification.

Certaines métriques ou métadonnées avancées peuvent être absentes (`null`) lorsque la fonctionnalité associée n’est pas encore totalement implémentée ou non applicable au scénario traité.

Les paramètres globaux du laboratoire ont été centralisés afin de préparer une future externalisation via configuration Spring.

Certaines requêtes progressives fournies dans les exemples ont nécessité l’ajout de données complémentaires afin de rester cohérentes avec les contraintes métier décrites dans les spécifications intermédiaires.

Ces ajustements ont uniquement consisté à compléter les données manquantes nécessaires au fonctionnement du moteur, sans modifier la structure ni la nomenclature des attributs définis dans les exemples fournis.

Certaines structures de données liées à la version INTERMEDIATE
(métadonnées, métriques avancées, etc.) sont déjà présentes dans
le modèle objet afin de préparer l’évolution progressive du moteur
de planification et garantir la compatibilité des futures extensions.

Les champs actuellement non implémentés sont volontairement conservés
dans le modèle mais ne sont pas sérialisés dans les réponses JSON
tant qu’aucune valeur pertinente n’est disponible.
---

# Gestion actuelle des pauses déjeuner

Les pauses déjeuner sont actuellement gérées comme des périodes d’indisponibilité fixes dans le planning des techniciens.

Cette approche garantit que :

* aucune nouvelle analyse ne peut démarrer pendant une pause déjeuner,
* une analyse déjà commencée avant la pause peut se poursuivre sans interruption,
* les analyses de priorité `STAT` ne sont jamais interrompues.

Le report dynamique complet des pauses déjeuner (afin de préserver automatiquement une heure complète de pause lorsqu’une analyse déborde) n’a pas été implémenté dans le temps imparti.

L’architecture actuelle du moteur de planification permet néanmoins d’ajouter cette fonctionnalité ultérieurement sans refonte majeure.

---

# Calcul de l’efficacité (efficiency)

La métrique `efficiency` est calculée conformément à la formule définie dans les spécifications intermédiaires :

```text
efficiency =
(Σ temps d’occupation des ressources
 / nombre de techniciens
 / durée totale du planning)
× 100
```

Cette métrique représente le taux moyen d’occupation des ressources humaines sur la période totale de planification.

Une valeur élevée indique une bonne utilisation globale des techniciens disponibles.

---

# Justification des choix algorithmiques

Le moteur de planification repose sur une approche greedy (opportuniste) :

* les échantillons sont traités par ordre de priorité,
* les ressources compatibles disponibles le plus tôt sont sélectionnées,
* les analyses sont parallélisées lorsque possible.

Cette approche a été privilégiée afin de :

* conserver un algorithme lisible et maintenable,
* faciliter l’évolution progressive entre les versions SIMPLE et INTERMEDIATE,
* éviter la complexité d’une optimisation exhaustive,
* garantir des performances suffisantes pour le périmètre demandé.

Le système repose sur des timelines de réservation (`ResourceTimeline`) permettant de gérer :

* les conflits,
* les périodes d’indisponibilité,
* les futures contraintes temporelles
  (maintenance, pauses, nettoyage, etc.).

---

# Limites et pistes d’amélioration

Cette première version a été volontairement conçue pour rester simple, lisible et cohérente avec le périmètre demandé dans la version SIMPLE du sujet.

L’objectif principal était de fournir une solution fonctionnelle, claire et facilement maintenable dans le temps imparti.

## Limites actuelles de l’algorithme

L’algorithme implémenté repose actuellement principalement sur une logique de compatibilité par catégorie d’analyse (`BLOOD`, `CHEMISTRY`, `MICROBIOLOGY`, etc.).

Cette approche permet de couvrir :

* les cas historiques V1,
* les exemples progressifs,
* ainsi qu’une partie importante des besoins métier décrits.

Cependant, le jeu de données complet « Données Moyennes » introduit un niveau métier plus avancé basé sur :

* les `analysisType`,
* les `compatibleTypes` des équipements,
* ainsi qu’une logique de compatibilité plus fine entre analyses et automates.

L’implémentation complète de cette couche nécessiterait :

* une normalisation métier des types d’analyses,
* une stratégie de matching avancée,
* ainsi qu’une refonte partielle du moteur de planification.

Compte tenu du temps imparti pour le test technique, le choix a été fait de privilégier :

* la stabilité de l’API,
* la rétrocompatibilité V1/V2,
* la cohérence du modèle métier,
* ainsi qu’une base d’algorithme propre et extensible.

L’architecture actuelle a néanmoins été pensée pour permettre l’ajout futur de cette logique avancée sans remise en cause majeure du modèle existant.

## Pistes d’amélioration futures

Une future version pourrait notamment inclure :

* une gestion des erreurs plus avancée avec des exceptions métier dédiées,
* des retours plus détaillés concernant les manques de ressources,
* une stratégie de planification plus optimisée,
* une meilleure gestion des conflits et des cas limites,
* une persistance des données via une base de données,
* une gestion dynamique des arrivées d’échantillons en temps réel,
* des métriques plus avancées sur l’utilisation des ressources,
* une gestion avancée des contraintes métier,
* le support complet du dataset « Données Moyennes »,
* un moteur de compatibilité basé sur `analysisType` et `compatibleTypes`.

---

# Tests

Le projet inclut actuellement :

- des tests d’intégration Spring Boot via `MockMvc`,
- des tests unitaires ciblés sur la logique de scheduling,
- la validation des principaux cas métier demandés dans les spécifications :
  - cas nominal,
  - respect des priorités,
  - compatibilité des spécialisations,
  - gestion des pauses déjeuner,
  - prise en compte des coefficients d’efficacité.

Compte tenu du temps imparti pour le test technique, la stratégie retenue a été de privilégier :

- quelques scénarios représentatifs et stables,
- la validation du pipeline complet de planification,
- ainsi que les composants métier les plus critiques du moteur de scheduling.

L’architecture actuelle permet néanmoins l’ajout simple de tests plus avancés (tests de charge, cas limites, datasets complexes, tests de non-régression, etc.).