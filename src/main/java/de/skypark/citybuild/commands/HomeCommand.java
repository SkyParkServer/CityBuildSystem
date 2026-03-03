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
      plugin.messages().error(sender, "Only players can run this command.");
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
                player, "Home names must be 1-16 chars and cannot contain spaces or path symbols.");
        return true;
      }

      if (!player.hasPermission("cb.home.home")) {
        plugin.messages().error(player, "You do not have permission to teleport to homes.");
        return true;
      }

      homes.teleportTo(player, homeName);
      return true;
    }

    homes.openMainMenu(player);
    return true;
  }
}
