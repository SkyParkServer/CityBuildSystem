# Getting Started

## Voraussetzungen

- Java 21
- Git
- Zugriff auf MySQL

## Projekt starten

```bash
./gradlew build
./gradlew runServer
```

`runServer` bereitet automatisch den lokalen Testserver vor und laedt benoetigte freie Plugins.

## PlotSquared Hinweis

PlotSquared (v7, paid) muss manuell in `server/plugins` gelegt werden.

## Lokale Qualitaetschecks

```bash
./gradlew spotlessApply
./gradlew spotlessCheck build
```
