# CityBuildSystem Dokumentation

Willkommen zur technischen Dokumentation des `CityBuildSystem` Plugins.

Diese Doku deckt den aktuellen Stand des Plugins ab:

- Build- und Entwicklungssetup
- Systemarchitektur
- Konfigurationen
- Command-Referenz
- Beitrag zum Projekt

## Tech Stack

- Paper `1.21.11`
- Java `21`
- Gradle (Kotlin DSL)
- MySQL
- YAML Konfigurationen

## Wichtige Regeln

- Keine Caches fuer wirtschaftliche/spielerbezogene Werte
- Synchrone DB-Operationen (Main-Thread) nach Projektvorgabe
- Nachrichten deutsch und hardcoded mit Legacy `&`-Farbcodes

## Links

- Repository: `SkyParkServer/CityBuildSystem`
- Lokaler Build:

```bash
./gradlew spotlessCheck build
```
