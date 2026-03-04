# Konfiguration

## config.yml

Zentrale globale Einstellungen:

- `const.prefix`: globaler Nachrichten-Prefix
- `const.no-perm`: Standard-No-Permission Text
- Datenbank-Block (`host`, `port`, `database`, `user`, `password`)
- `server-name`: Kennung fuer servergebundene Daten (z. B. Warps)
- `update-checker.enabled`: Versionspruefung beim Start an/aus
- `update-checker.repository`: GitHub Repo fuer den Versionsvergleich
- `update-checker.timeout-ms`: Timeout fuer GitHub API Request

## homes.yml

Steuert Home-System Grenzen und Kauf-Logik:

- Homes pro Rang
- Basispreis und Preissteigerung
- maximales Gesamtlimit

## spawn.yml

Steuert Spawn-Verhalten:

- gespeicherte Spawn-Position
- `teleport-on-join` (Join-Teleport aktiv/inaktiv)

## farm.yml

Steuert das Farmwelt-Menu (`/farm`, `/farmwelt`):

- `menu.title`: GUI-Titel
- `menu.ping-timeout-ms`: Timeout fuer Server-Statusabfragen
- `menu.close-item.*`: Item unten mittig (Slot 49) zum Schliessen
- `menu.status.online` / `menu.status.maintenance`: Status-Texte fuer Placeholders
- `servers.<key>.*`: einzelner Farm-Server (Servername, Host/Port, Slot, Item, Name, Lore)
- `servers.<key>.maintenance-markers`: Woerter, die im Ping-Response als Wartung erkannt werden

Unterstuetzte Placeholders in Name/Lore:

- `%server%`, `%server_key%`
- `%online_players%` (Alias `%online%`)
- `%maintenance_status%` (Alias `%maintenance%`)
- `%motd%`

## Empfehlung

- Alle Datei-Aenderungen versionieren
- Keine Zugangsdaten im Klartext committen
