package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.core.FarmMenuService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FarmCommand implements CommandExecutor {

  private final CityBuildSystem plugin;
  private final FarmMenuService farmMenuService;

  public FarmCommand(CityBuildSystem plugin, FarmMenuService farmMenuService) {
    this.plugin = plugin;
    this.farmMenuService = farmMenuService;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }

    if (!sender.hasPermission("cb.farm.use")) {
      plugin.messages().error(sender, plugin.settings().noPermissionMessage());
      return true;
    }

    farmMenuService.openMenu(player);
    return true;
  }
}
