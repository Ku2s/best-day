# Best Day

Application Spring Boot qui recommande **le meilleur jour des 7 prochains** pour pratiquer une
activité de plein air (running, cycling, picnic, beach) dans une ville donnée, à partir des
prévisions météo de l'API publique [Open-Meteo](https://open-meteo.com/) (gratuite, sans clé).

Projet réalisé dans le cadre de l'examen **M1 MIAGE — DevOps 2 (session 2)**.

## Objectif

Exposer une route REST qui, pour une ville et une activité, calcule un **score de confort (0-100)**
pour chacun des 7 prochains jours et renvoie le meilleur jour ainsi que le classement complet.

## API REST

```
GET /api/best-day?city=Paris&activity=running
```

| Paramètre  | Obligatoire | Défaut    | Description                                      |
|------------|-------------|-----------|--------------------------------------------------|
| `city`     | oui         | —         | Nom de la ville (géocodée via Open-Meteo)        |
| `activity` | non         | `running` | `running`, `cycling`, `picnic` ou `beach`        |

La réponse contient le meilleur jour et le classement des 7 jours, chaque jour ayant un score
(0-100) et une raison lisible.

## Fonctionnalité métier

Pour chaque jour, le service calcule un score de confort = **moyenne pondérée de 3 sous-scores**
bornés 0-100 :

- **température** (écart à la température idéale propre à l'activité),
- **précipitations**,
- **vent**,

puis classe les jours du meilleur au moins bon. L'appel à Open-Meteo (géocodage de la ville puis
prévisions journalières) est réalisé **à l'intérieur de la chaîne métier**, pas dans le contrôleur.

## Stack technique

- Java 17
- Spring Boot 3.3.x (Spring Web)
- Maven
- API REST distante : Open-Meteo (géocodage + prévisions journalières)

## Front-end (bonus)

Une page statique (`src/main/resources/static/index.html`) servie par Spring Boot offre un
formulaire (ville + activité) et affiche le meilleur jour et le classement des 7 jours. Elle
appelle l'API REST en `fetch` sur la même origine. Une fois l'application lancée, ouvrir :

```
http://localhost:8080/
```

## Lancer le projet

```bash
mvn spring-boot:run
```

Puis ouvrir <http://localhost:8080/> dans un navigateur, ou appeler l'API directement :

```bash
curl "http://localhost:8080/api/best-day?city=Paris&activity=running"
```

## Architecture

```
controller  -> REST endpoint /api/best-day
service     -> calcul du score de confort + classement des jours
client      -> appels HTTP vers Open-Meteo (géocodage + prévisions)
```

Pas de base de données ni de persistance.
