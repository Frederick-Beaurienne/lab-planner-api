# Laboratoire - Système de Planification

## Sommaire

* [Description](#description)
* [Installation](#installation)
* [Utilisation](#utilisation)
* [Évolution depuis version SIMPLE](#évolution-depuis-version-simple)
* [Limites et pistes d’amélioration](#limites-et-pistes-damélioration)
* [Validation des données](#validation-des-données)

## Description

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
  * conflits détectés.

## Installation

### Prérequis

* Java 21
* Maven 3+

### Lancer le projet

```bash
./mvnw spring-boot:run
```

L’API sera accessible sur :

```text
http://localhost:8080
```

### Swagger

Documentation Swagger disponible sur :

```text
http://localhost:8080/swagger-ui.html
```

## Utilisation

### Endpoint principal

```http
POST /api/planning
```

### Exemple de requête

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

### Tester l’API avec Swagger

Une fois l’application lancée, la documentation interactive Swagger est accessible sur :

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger permet :

* de visualiser les endpoints disponibles,
* de consulter les modèles JSON,
* de tester directement les requêtes depuis le navigateur.

### Tester l’API avec Insomnia

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

## Évolution depuis version SIMPLE

Le projet suit volontairement une architecture Spring Boot simple et lisible afin de rester cohérent avec le périmètre demandé.

Améliorations ajoutées :

* API REST Spring Boot,
* documentation Swagger/OpenAPI,
* séparation Controller / Service / Model,
* tri des échantillons par priorité puis heure d’arrivée,
* détection automatique des conflits de ressources,
* calcul des métriques du planning.

### Remarque concernant l’efficacité

Le calcul d’efficacité implémenté suit strictement la formule fournie dans les spécifications :

```text
efficiency = (somme des durées d’analyses / durée totale du planning) * 100
```

Dans les cas où plusieurs analyses sont exécutées en parallèle, cette formule peut produire un résultat supérieur à 100%.

Ce comportement est cohérent avec la définition fournie dans l’énoncé et reflète le niveau d’utilisation cumulé des ressources en situation de parallélisme.

### Validation des données

La validation des données d’entrée a été volontairement alignée sur les exemples fournis dans l’énoncé.

Certains champs descriptifs comme `name` ont été considérés comme optionnels lorsque les exemples de données ne les renseignaient pas systématiquement.

### Limites et pistes d’amélioration

Cette première version a été volontairement conçue pour rester simple, lisible et cohérente avec le périmètre demandé dans la version SIMPLE du sujet.

L’objectif principal était de fournir une solution fonctionnelle, claire et facilement maintenable dans le temps imparti.

Une future version pourrait notamment inclure :

* une gestion des erreurs plus avancée avec des exceptions métier dédiées,
* des retours plus détaillés concernant les manques de ressources (techniciens indisponibles, équipements insuffisants, dépassement des horaires de travail, etc.),
* une stratégie de planification plus optimisée,
* une meilleure gestion des conflits et des cas limites,
* l’ajout de tests unitaires et d’intégration plus complets,
* une persistance des données via une base de données,
* une gestion dynamique des arrivées d’échantillons en temps réel,
* des métriques plus avancées sur l’utilisation des ressources.
