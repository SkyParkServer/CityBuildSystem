package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.core.HomeService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {

  private final CityBuildSystem plugin;
  private final HomeService homes;

  public SetHomeCommand(CityBuildSystem plugin, HomeService homes) {
    this.plugin = plugin;
    this.homes = homes;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }

    if (!sender.hasPermission("cb.home.set.use")) {
      plugin.messages().error(sender, plugin.settings().noPermissionMessage());
      return true;
    }

    if (args.length < 1) {
      plugin.messages().error(player, "Nutze: /sethome <name>");
      return true;
    }

    String homeName = args[0];
    if (!homes.isNameValid(homeName)) {
      plugin
          .messages()
          .error(
              player,
              "Home-Namen muessen 1-16 Zeichen lang sein und duerfen keine Leer- oder Pfadzeichen enthalten.");
      return true;
    }

    boolean existing = homes.exists(player, homeName);
    if (!existing) {
      int current = homes.count(player);
      int limit = homes.totalLimit(player);
      if (current >= limit) {
        plugin.messages().error(player, "Du hast dein Home-Limit erreicht (&e" + limit + "&c).");
        return true;
      }
    }

    homes.saveHome(player, homeName);
    homes.refreshNameCache(player);
    plugin.messages().success(player, "Home &e" + homeName + "&a wurde gesetzt.");
    return true;
  }
}
