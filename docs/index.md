# CityBuildSystem Dokumentation

Willkommen zur technischen Dokumentation des `CityBuildSystem` Plugins.

Diese Doku ist in zwei Hauptbereiche getrennt:

- `Usage`: Installation, Konfiguration, Commands und Feature-Verhalten
- `Development`: Setup fuer Entwickler, Struktur, Konventionen und CI/CD

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

## Schnellstart

- Fuer Betreiber: `Usage -> Voraussetzungen und Setup`
- Fuer Entwickler: `Development -> Dev Setup`

## Links

- Repository: `SkyParkServer/CityBuildSystem`
- Lokaler Build:

```bash
./gradlew spotlessCheck build
```
