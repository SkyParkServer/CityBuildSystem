package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.core.HomeService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

  private final CityBuildSystem plugin;
  private final HomeService homes;

  public HomeCommand(CityBuildSystem plugin, HomeService homes) {
    this.plugin = plugin;
    this.homes = homes;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }

    if (!sender.hasPermission("cb.home.use")) {
      plugin.messages().error(sender, plugin.settings().noPermissionMessage());
      return true;
    }

    if (args.length >= 1) {
      String homeName = args[0];
      if (!homes.isNameValid(homeName)) {
        plugin
            .messages()
            .error(
                player,
                "Home-Namen muessen 1-16 Zeichen lang sein und duerfen keine Leer- oder Pfadzeichen enthalten.");
        return true;
      }

      if (!player.hasPermission("cb.home.home")) {
        plugin.messages().error(player, "Du hast keine Rechte, um zu Homes zu teleportieren.");
        return true;
      }

      homes.teleportTo(player, homeName);
      return true;
    }

    homes.openMainMenu(player);
    return true;
  }
}
