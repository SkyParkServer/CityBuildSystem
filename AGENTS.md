# CityBuild System - Agent Guide

Dieses Dokument ist die verbindliche Arbeitsgrundlage fuer alle AI-Agents in diesem Repository.

## 1) Projektziel

Komplettes CityBuild-System fuer ein Minecraft-Netzwerk mit serveruebergreifenden Features auf Basis von Paper + MySQL.

## 2) Tech Stack

- Server: Paper 1.21.11
- Sprache: Java
- Build: Gradle (Kotlin DSL)
- Datenbank: MySQL
- Konfiguration: YAML
- Proxy: Velocity

## 3) Harte Architekturregeln (immer einhalten)

1. No external libraries
   - Erlaubt: Paper/Spigot API, MySQL-Driver.
   - Ausnahme: Lombok nur compile-time (`io.freefair.lombok`), kein Runtime-Zwang.
2. MySQL-Regeln
   - SQL inline im Code (kein ORM, keine externen SQL-Files).
   - Kein Caching fuer spielrelevante Daten.
   - Daten direkt lesen/schreiben.
3. Multi-Server-Verhalten
   - Features muessen serveruebergreifend ueber DB funktionieren, wenn fachlich gefordert.
4. Nachrichtenformat
   - Alle Spielermeldungen auf Deutsch, hardcoded.
   - Legacy-Farbcodes mit `&`, kein MiniMessage.

## 4) Coding-Konventionen im Projekt

- Einstiegspunkt: `src/main/java/de/skypark/citybuild/CityBuildSystem.java`
- Viele Services/Stores sind per Lombok fluent erreichbar (`plugin.messages()`, `plugin.settings()`, ...).
- Commands werden zentral in `CityBuildSystem#onEnable` registriert.
- Brigadier-Vorschlaege laufen ebenfalls zentral in `CityBuildSystem`.
- Persistente Tabellen werden in `DatabaseManager#initSchema()` angelegt.

## 5) Wichtige bestehende Systeme (Stand jetzt)

- Tresor-System
  - Command: `/tresor`
  - 8 Seiten, Permissions `cb.tresor.seite.1` bis `cb.tresor.seite.8`
  - Shared ueber MySQL (`cb_tresor`), 10s Open-Cooldown
- Vanish-System
  - Commands: `/v`, `/vanish`, `/vanish <spieler>`
  - Permission: `ch.vanish.use`
  - Vanished Spieler sind nicht targetbar/sichtbar (auch nicht fuer OP / `*`)
  - Sichtbarkeit in Tab-Completion/Targeting ueber `VanishService`
- Version-Checker
  - Startet asynchron beim Plugin-Start
  - Prueft GitHub Release/Tag gegen aktuelle Plugin-Version
  - Konfigurierbar ueber `update-checker.*` in `config.yml`

## 6) Konfiguration und Docs

Nach jedem neuen Feature muessen die Docs zwingend direkt mit aktualisiert werden (kein spaeteres Nachziehen).
Bei Feature-Aenderungen immer pruefen und bei Bedarf anpassen:

- `src/main/resources/plugin.yml` (Command/Permission)
- `src/main/resources/config.yml` (Defaults)
- `docs/usage/commands.md`
- `docs/usage/features.md`
- `docs/usage/configuration.md`

## 7) Release- und CI-Workflow

- Vor Release Version in `build.gradle.kts` erhoehen.
- Qualitaetscheck lokal: `./gradlew spotlessCheck compileJava`
- Release wird per Tag `v*` gebaut (`.github/workflows/release.yml`).
- Release-Workflow ist idempotent:
  - existiert Tag-Release bereits, werden Assets/Notes aktualisiert,
  - sonst wird Release neu erstellt.

## 8) Agent-Checkliste vor Abschluss

1. Regeln aus Abschnitt 3 eingehalten?
2. Feature in `CityBuildSystem` sauber verdrahtet (Service/Listener/Command)?
3. `plugin.yml` und ggf. `config.yml` aktualisiert?
4. Doku fuer jedes neue Feature verpflichtend aktualisiert?
5. `./gradlew spotlessCheck compileJava` erfolgreich?
6. Bei Release: Version bump + Tag-Flow korrekt?

## 9) Third-Party Integrationen (vorhanden)

- LuckPerms
- Vault
- PlotSquared
- WorldEdit
- MultiverseCore
