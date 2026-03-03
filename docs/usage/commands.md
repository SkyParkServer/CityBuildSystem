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
| `/speed <1-10>` | Lauf-/Fluggeschwindigkeit | `cb.speed.use` |

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
