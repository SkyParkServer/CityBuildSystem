# Konfiguration

## config.yml

Zentrale globale Einstellungen:

- `const.prefix`: globaler Nachrichten-Prefix
- `const.no-perm`: Standard-No-Permission Text
- Datenbank-Block (`host`, `port`, `database`, `user`, `password`)
- `server-name`: Kennung fuer servergebundene Daten (z. B. Warps)

## homes.yml

Steuert Home-System Grenzen und Kauf-Logik:

- Homes pro Rang
- Basispreis und Preissteigerung
- maximales Gesamtlimit

## spawn.yml

Steuert Spawn-Verhalten:

- gespeicherte Spawn-Position
- `teleport-on-join` (Join-Teleport aktiv/inaktiv)

## Empfehlung

- Alle Datei-Aenderungen versionieren
- Keine Zugangsdaten im Klartext committen
