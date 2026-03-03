package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

  private final CityBuildSystem plugin;

  public SpawnCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }

    if (!player.hasPermission("cb.spawn.use")) {
      plugin.messages().message(player, plugin.settings().noPermissionMessage());
      return true;
    }

    Location spawn = plugin.spawnManager().getSpawn();
    if (spawn == null) {
      plugin.messages().error(player, "Spawn ist nicht gesetzt.");
      return true;
    }

    player.teleport(spawn);
    plugin.messages().success(player, "Teleportiert.");
    return true;
  }
}
