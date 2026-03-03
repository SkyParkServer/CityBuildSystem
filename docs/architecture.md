# Architektur

## Module-Ueberblick

- `de.skypark.citybuild.CityBuildSystem`: Plugin Bootstrap
- `core/*`: Konfigurations- und Domain-Services
- `storage/*`: MySQL-Zugriffsschicht (Stores)
- `commands/*`: Command-Implementierungen
- `listeners/*`: Event-Handling
- `util/*`: Hilfsklassen

## Datenhaltung

- Direkter MySQL-Zugriff mit Inline-SQL
- Keine ORM-Schicht
- Kein Runtime-Cache fuer spielrelevante Daten

## Infrastruktur

- `build.gradle.kts` als Single-Module Build
- Versionen zentral in `gradle/libs.versions.toml`
- CI/CD via GitHub Actions
