# Dev Setup

## Voraussetzungen

- Java `21`
- Git
- Zugang zur Testdatenbank (MySQL)

## Start

```bash
./gradlew build
./gradlew runServer
```

## Qualitaetschecks

```bash
./gradlew spotlessApply
./gradlew spotlessCheck build
```

## Plugins lokal

- Freie Plugins werden beim ersten `runServer` automatisch geladen.
- PlotSquared v7 (paid) manuell nach `server/plugins` legen.
