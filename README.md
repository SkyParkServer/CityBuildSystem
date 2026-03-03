# CityBuildSystem

CityBuildSystem is a private Minecraft Paper plugin for the SkyPark network.

## Status

- Java 21 + Gradle Kotlin DSL
- Paper `1.21.11`
- CI with build + formatting checks
- Automatic code formatting via Spotless

## Development

Run local checks before opening a pull request:

```bash
./gradlew spotlessApply
./gradlew spotlessCheck build
```

## Contributing

Pull requests are welcome from authorized collaborators.

By contributing, you agree that your changes can be used, modified, and relicensed by the project owner without compensation.

See `CONTRIBUTING.md` for workflow details.

## Documentation

- Source files: `docs/`
- Site config: `mkdocs.yml`
- GitHub Pages deploy workflow: `.github/workflows/docs.yml`
- Struktur:
  - `Usage`: Betrieb, Setup, Konfiguration, Commands, Features
  - `Development`: Setup, Struktur, Konventionen, Libraries, CI/CD

After enabling GitHub Pages (Source: GitHub Actions), docs are published automatically on pushes affecting docs.

## License

This project is source-available and private.

No permission is granted to use, copy, modify, run, distribute, or sublicense this software for commercial or private use outside this repository, except for contribution through pull requests as explicitly allowed in `LICENSE`.
