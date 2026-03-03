# Entwicklung

## Code Style

Wir verwenden Spotless fuer konsistente Formatierung.

```bash
./gradlew spotlessApply
./gradlew spotlessCheck build
```

## Branching

- Kleine, fokussierte Pull Requests
- Saubere Commit Messages

## CI

Standard-Checks:

- Gradle Wrapper Validation
- Spotless Check
- Build

## Releases

Tags nach Schema `v*` erzeugen automatisch einen GitHub Release-Workflow.
