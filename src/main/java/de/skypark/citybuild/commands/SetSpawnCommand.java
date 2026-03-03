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
      plugin.messages().error(sender, "Only players can run this command.");
      return true;
    }

    if (!player.hasPermission("cb.spawn.set.use")) {
      player.sendMessage(plugin.messages().color("§6§lSkyPark §8» §7Du hast dazu keine Rechte!"));
      return true;
    }

    plugin.spawnManager().setSpawn(player.getLocation());
    plugin.messages().success(player, "Spawn gesetzt.");
    return true;
  }
}
