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
      plugin.messages().message(sender, "&7CityBuild dev command");
      plugin.messages().message(sender, "&8- &e/cb reload &7Reload all CityBuild scripts");
      plugin.messages().message(sender, "&8- &e/cb debug <on|off|toggle> &7Set debug mode");
      plugin.messages().message(sender, "&8- &e/cb info &7Show quick status");
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
      plugin.messages().success(sender, "Requested Skript reload for all scripts.");
      return true;
    }

    if (action.equals("debug")) {
      if (state == null) {
        boolean enabled = plugin.globals().debugEnabled();
        plugin.globals().setDebugEnabled(!enabled);
        plugin
            .messages()
            .success(sender, !enabled ? "Debug mode enabled." : "Debug mode disabled.");
        return true;
      }

      if (state.equals("on")) {
        plugin.globals().setDebugEnabled(true);
        plugin.messages().success(sender, "Debug mode enabled.");
        return true;
      }

      if (state.equals("off")) {
        plugin.globals().setDebugEnabled(false);
        plugin.messages().success(sender, "Debug mode disabled.");
        return true;
      }

      if (state.equals("toggle")) {
        boolean enabled = plugin.globals().debugEnabled();
        plugin.globals().setDebugEnabled(!enabled);
        plugin
            .messages()
            .success(sender, !enabled ? "Debug mode enabled." : "Debug mode disabled.");
        return true;
      }

      plugin.messages().error(sender, "Usage: /cb debug <on|off|toggle>");
      return true;
    }

    if (action.equals("info")) {
      plugin.messages().message(sender, "&7Debug: &e" + plugin.globals().debugEnabled());
      boolean spawnSet =
          plugin.globals().spawnLocationText() != null
              && !plugin.globals().spawnLocationText().isEmpty();
      if (spawnSet) {
        plugin.messages().message(sender, "&7Spawn: &aconfigured");
      } else {
        plugin.messages().message(sender, "&7Spawn: &cnot configured");
      }
      return true;
    }

    plugin.messages().error(sender, "Unknown subcommand. Use /cb");
    return true;
  }
}
