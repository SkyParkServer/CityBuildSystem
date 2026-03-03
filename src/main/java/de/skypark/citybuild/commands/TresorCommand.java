package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.core.TresorService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TresorCommand implements CommandExecutor {

  private final CityBuildSystem plugin;
  private final TresorService tresorService;

  public TresorCommand(CityBuildSystem plugin, TresorService tresorService) {
    this.plugin = plugin;
    this.tresorService = tresorService;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }

    if (!player.hasPermission("cb.tresor.use")) {
      plugin.messages().error(player, plugin.settings().noPermissionMessage());
      return true;
    }

    tresorService.openFromCommand(player);
    return true;
  }
}
