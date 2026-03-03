# Contributing

Thanks for contributing to CityBuildSystem.

## Local checks before opening a PR

Run these commands locally and ensure they pass:

```bash
./gradlew spotlessApply
./gradlew spotlessCheck build
```

- `spotlessApply` formats source files automatically.
- `spotlessCheck` verifies formatting in the same way CI does.
- `build` compiles and runs all configured checks.

## Pull request notes

- Keep PRs focused and small when possible.
- Use clear commit messages.
- If CI reports formatting issues, run `./gradlew spotlessApply` and push again.
