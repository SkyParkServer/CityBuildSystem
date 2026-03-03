# Konventionen

## Architekturregeln

- Keine externen Runtime-Libraries ausser Paper API und MySQL Driver
- Lombok nur compile-time (via `io.freefair.lombok`)
- Inline-SQL in den Stores
- Keine Cache-Schicht fuer spielrelevante Werte

## Sprachregeln

- Spielertexte deutsch
- Legacy-Farbcodes mit `&`
- Kein MiniMessage

## Coding Style

- Spotless + google-java-format ist verpflichtend
- Vor PR immer `spotlessCheck build`
- Keine unnoetigen Kommentare, nur fuer nicht offensichtliche Logik

## Git/PR

- Kleine, fokussierte PRs
- Aussagekraeftige Commit Messages
- Keine sensiblen Dateien (z. B. `.env`) committen
