# Commands

Die Command-Referenz ist nach Rollen aufgeteilt.

## User

| Command | Beschreibung | Permission |
|---|---|---|
| `/wb`, `/workbench`, `/craft` | Virtuelle Werkbank | `cb.workbench.use` |
| `/anvil` | Virtueller Amboss | `cb.anvil.use` |
| `/repair` | Item in Hand reparieren | `cb.repair.use` |
| `/wetter <regen\|gewitter\|sonne>` | Weltwetter setzen | `cb.wetter.use` |
| `/hat` | Item aus Hand als Helm | `cb.hat.use` |
| `/ec`, `/enderchest` | Serveruebergreifende Enderchest | `cb.enderchest.use` |
| `/tresor` | Serveruebergreifender Tresor (8 Seiten) | `cb.tresor.use` |
| `/v`, `/vanish` | Eigenen Vanish toggeln | `ch.vanish.use` |
| `/speed <1-10>` | Lauf-/Fluggeschwindigkeit | `cb.speed.use` |

### Tresor Permissions

| Permission | Beschreibung |
|---|---|
| `cb.tresor.use` | Oeffnet den Tresor |
| `cb.tresor.seite.1` | Seite 1 freischalten |
| `cb.tresor.seite.2` | Seite 2 freischalten |
| `cb.tresor.seite.3` | Seite 3 freischalten |
| `cb.tresor.seite.4` | Seite 4 freischalten |
| `cb.tresor.seite.5` | Seite 5 freischalten |
| `cb.tresor.seite.6` | Seite 6 freischalten |
| `cb.tresor.seite.7` | Seite 7 freischalten |
| `cb.tresor.seite.8` | Seite 8 freischalten |

## Homes & Teleport

| Command | Beschreibung | Permission |
|---|---|---|
| `/spawn` | Zum Spawn teleportieren | `cb.spawn.use` |
| `/setspawn` | Spawnpunkt setzen | `cb.spawn.set.use` |
| `/home`, `/home <name>` | Home GUI / Teleport | `cb.home.use`, `cb.home.home` |
| `/sethome <name>` | Home setzen | `cb.home.set.use` |
| `/delhome <name>` | Home loeschen | `cb.home.del.use` |
| `/setwarp <name>` | Warp setzen | `cb.warp.set` |
| `/warp <name>`, `/warps` | Warp nutzen/listen | `cb.warp.warps` |
| `/delwarp <name>` | Warp loeschen | `cb.warp.del` |

## Kommunikation

| Command | Beschreibung | Permission |
|---|---|---|
| `/msg <spieler> <nachricht>` | Private Nachricht | `cb.msg.use` |
| `/r <nachricht>` | Reply auf letzte Konversation | `cb.msg.reply.use` |
| `/msgtoggle` | PMs an/aus | `cb.msg.msgtoggle` |
| `/werbung <nachricht>` | Plot-Werbung mit TP-Link | `cb.werbung.use` |

## Moderation / Team

| Command | Beschreibung | Permission |
|---|---|---|
| `/invsee <spieler>` | Inventar einsehen | `cb.invsee.use` |
| `/vanish <spieler>` | Spieler in Vanish setzen | `ch.vanish.use` |
| `/near` | Spieler in Naehe auflisten | `cb.near.use` |
| `/feed [spieler]` | Hunger auffuellen | `cb.feed.use`, `cb.feed.other` |
| `/heal [spieler]` | Heilen | `cb.heal.use`, `cb.heal.other.use` |

## Economy / Admin

| Command | Beschreibung | Permission |
|---|---|---|
| `/balance`, `/money` | Kontostand anzeigen | `cb.balance.use` |
| `/eco ...` | Economy Admin | `citybuild.admin.economy` |
| `/bank ...` | Bankkonto verwalten | `cb.bank.*` |
| `/kristalle ...` | Kristalle verwalten | `cb.kristalle.admin` |
| `/cb ...` | System-Admin Utilities | `citybuild.admin` |

> Finale und verbindliche Command-Registrierung: `src/main/resources/plugin.yml`.
