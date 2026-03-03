# CI/CD und Releases

## Workflows

- `ci.yml` - Wrapper Validation, Spotless Check, Build, Artifact Upload
- `dependency-review.yml` - PR Dependency Safety
- `auto-format.yml` - automatische Spotless-Formatierung fuer PRs
- `release.yml` - Build + GitHub Release bei Tags `v*`
- `docs.yml` - MkDocs Build + GitHub Pages Deployment

## Release-Flow

1. Version im Build setzen
2. Tag erzeugen (`vX.Y.Z`)
3. `release.yml` erzeugt Release inklusive Changelog aus Commits
4. Falls ein Release mit demselben Tag bereits existiert, werden Assets/Notes aktualisiert (kein Hard-Fail)

## Docs-Deployment

- Push auf `master`/`main` mit Aenderungen in `docs/**` oder `mkdocs.yml` deployed GitHub Pages automatisch.
