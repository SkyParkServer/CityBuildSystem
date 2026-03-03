package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class WorkbenchCommand implements CommandExecutor {
  private final CityBuildSystem plugin;

  public WorkbenchCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Only players can run this command.");
      return true;
    }

    if (!player.hasPermission("cb.workbench.use")) {
      plugin.messages().error(player, plugin.settings().noPermissionMessage());
      return true;
    }

    player.openWorkbench(null, true);
    return true;
  }
}
