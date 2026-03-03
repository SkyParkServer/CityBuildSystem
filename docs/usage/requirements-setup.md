# Voraussetzungen und Setup

## Anforderungen

- Java `21`
- Paper Server `1.21.11`
- MySQL Datenbankzugang
- Zugriff auf das Repository

## Lokaler Start (Development Server)

```bash
./gradlew build
./gradlew runServer
```

`runServer` richtet den lokalen Testserver ein und laedt benoetigte freie Plugins beim ersten Lauf automatisch.

## Drittanbieter-Plugins

Automatisch geladen:

- LuckPerms
- Vault
- FastAsyncWorldEdit
- Multiverse-Core

Manuell (paid):

- PlotSquared v7 muss in `server/plugins` gelegt werden.

## Produktion

- Stelle sicher, dass alle Dependencies als Plugin/JAR verfuegbar sind.
- Pruefe Datenbank-Erreichbarkeit und Zugangsdaten vor dem ersten Join.
