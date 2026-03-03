package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

  private final CityBuildSystem plugin;

  public SetSpawnCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }

    if (!player.hasPermission("cb.spawn.set.use")) {
      plugin.messages().message(player, plugin.settings().noPermissionMessage());
      return true;
    }

    plugin.spawnManager().setSpawn(player.getLocation());
    plugin.messages().success(player, "Spawn gesetzt.");
    return true;
  }
}
