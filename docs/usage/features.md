# Features

## Utility

- Virtuelle Werkbank und Amboss
- Item-Reparatur
- Wettersteuerung per Command
- Hat-System (Item als Helm)
- Speed-Control (1-10)
- Tresor-System mit 8 Seiten (`/tresor`)
- Vanish-System (`/v`, `/vanish`)
- Version-Checker gegen GitHub Releases beim Plugin-Start

## Homes, Spawn und Warps

- Spawnpunkt in `spawn.yml`
- Home-System mit Limits und Kauf-Logik aus `homes.yml`
- Servergebundene Warps via Datenbank (`server-name`)

## Kommunikation

- Globales PM-System (`/msg`, `/r`, `/msgtoggle`)
- Werbung mit klickbarer Teleportation
- Werbung nur auf eigenem/vertrautem Plot (PlotSquared v7 Check)

## Wirtschaft

- Money-System mit DB-Persistenz
- Bank-System (Ein-/Auszahlen)
- Kristalle-System fuer Adminverwaltung

## Team / Moderation

- `invsee` mit Read-only Modus
- `near` zur Umfeld-Analyse
- `feed`/`heal` fuer Self und Other
- Vanish blendet Spieler komplett aus (inkl. Tablist, Equipment und Hand-Items)

## Netzwerk-Sync

- Enderchest, Tresor, PM-Zustand und weitere Stores datenbankbasiert
- Keine Cache-Schicht fuer spielrelevante Werte

## Vanish Details

- `/v` oder `/vanish` toggelt den eigenen Vanish-Status
- `/vanish <spieler>` setzt einen sichtbaren Spieler in Vanish
- Permission: `ch.vanish.use`
- Vanished Spieler sind fuer andere nicht sichtbar und nicht targetbar
- Auch OP bzw. `*` sehen Vanish-Spieler nicht

## Tresor Details

- GUI mit 54 Slots, orangem Rahmen, Barriere unten mittig und Pfeilen fuer Seitenwechsel
- 8 Seiten mit Einzel-Permissions `cb.tresor.seite.1` bis `cb.tresor.seite.8`
- Gesperrte Seiten sind mit rotem Glas belegt
- Oeffnungs-Cooldown von 10 Sekunden pro Spieler
- Vollstaendig MySQL-basiert und serveruebergreifend synchron
