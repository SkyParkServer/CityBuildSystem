# Projektstruktur

## Hauptpakete

- `de.skypark.citybuild` - Plugin Entry + Wiring
- `commands` - alle Commands und TabCompleter
- `listeners` - Bukkit Event Handling
- `core` - fachliche Services / Manager
- `storage` - Datenbankzugriff (SQL Stores)
- `util` - Utility-Funktionen

## Ressourcen

- `src/main/resources/plugin.yml` - Command/Permission Registrierung
- `src/main/resources/config.yml` - globale Runtime-Konfiguration

## Build

- `build.gradle.kts` - Build und Tasks
- `gradle/libs.versions.toml` - zentrale Versionen
