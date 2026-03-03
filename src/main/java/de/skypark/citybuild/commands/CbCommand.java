package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CbCommand implements CommandExecutor {

  private final CityBuildSystem plugin;

  public CbCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!sender.hasPermission("citybuild.admin")) {
      plugin.messages().error(sender, plugin.settings().noPermissionMessage());
      return true;
    }

    String action = args.length >= 1 ? args[0].toLowerCase() : null;
    String state = args.length >= 2 ? args[1].toLowerCase() : null;

    if (action == null) {
      plugin.messages().message(sender, "&7CityBuild Admin-Befehl");
      plugin.messages().message(sender, "&8- &e/cb reload &7Laedt alle CityBuild-Skripte neu");
      plugin.messages().message(sender, "&8- &e/cb debug <on|off|toggle> &7Setzt den Debug-Modus");
      plugin.messages().message(sender, "&8- &e/cb info &7Zeigt den aktuellen Status");
      return true;
    }

    if (action.equals("reload")) {
      // Exact script behavior: execute console command "skript reload all"
      if (Bukkit.getPluginManager().getPlugin("Skript") != null) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skript reload all");
      } else {
        // Fallback: reload plugin config (best effort)
        plugin.reloadConfig();
      }
      plugin.messages().success(sender, "Skript-Reload fuer alle Skripte wurde ausgefuehrt.");
      return true;
    }

    if (action.equals("debug")) {
      if (state == null) {
        boolean enabled = plugin.globals().debugEnabled();
        plugin.globals().setDebugEnabled(!enabled);
        plugin
            .messages()
            .success(sender, !enabled ? "Debug-Modus aktiviert." : "Debug-Modus deaktiviert.");
        return true;
      }

      if (state.equals("on")) {
        plugin.globals().setDebugEnabled(true);
        plugin.messages().success(sender, "Debug-Modus aktiviert.");
        return true;
      }

      if (state.equals("off")) {
        plugin.globals().setDebugEnabled(false);
        plugin.messages().success(sender, "Debug-Modus deaktiviert.");
        return true;
      }

      if (state.equals("toggle")) {
        boolean enabled = plugin.globals().debugEnabled();
        plugin.globals().setDebugEnabled(!enabled);
        plugin
            .messages()
            .success(sender, !enabled ? "Debug-Modus aktiviert." : "Debug-Modus deaktiviert.");
        return true;
      }

      plugin.messages().error(sender, "Nutze: /cb debug <on|off|toggle>");
      return true;
    }

    if (action.equals("info")) {
      plugin.messages().message(sender, "&7Debug: &e" + plugin.globals().debugEnabled());
      boolean spawnSet =
          plugin.globals().spawnLocationText() != null
              && !plugin.globals().spawnLocationText().isEmpty();
      if (spawnSet) {
        plugin.messages().message(sender, "&7Spawn: &agesetzt");
      } else {
        plugin.messages().message(sender, "&7Spawn: &cnicht gesetzt");
      }
      return true;
    }

    plugin.messages().error(sender, "Unbekannter Unterbefehl. Nutze /cb");
    return true;
  }
}
