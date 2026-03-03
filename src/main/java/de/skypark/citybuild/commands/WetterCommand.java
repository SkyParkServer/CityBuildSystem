package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class WetterCommand implements CommandExecutor {
  private final CityBuildSystem plugin;

  public WetterCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Only players can run this command.");
      return true;
    }

    if (!player.hasPermission("cb.wetter.use")) {
      plugin.messages().error(player, plugin.settings().noPermissionMessage());
      return true;
    }

    if (args.length < 1) {
      plugin.messages().error(player, "Usage: /wetter <regen|gewitter|sonne>");
      return true;
    }

    String mode = args[0].toLowerCase();
    if (mode.equals("regen")) {
      player.getWorld().setStorm(true);
      player.getWorld().setThundering(false);
      plugin.messages().success(player, "Wetter gesetzt: Regen.");
      return true;
    }
    if (mode.equals("gewitter")) {
      player.getWorld().setStorm(true);
      player.getWorld().setThundering(true);
      plugin.messages().success(player, "Wetter gesetzt: Gewitter.");
      return true;
    }
    if (mode.equals("sonne")) {
      player.getWorld().setStorm(false);
      player.getWorld().setThundering(false);
      plugin.messages().success(player, "Wetter gesetzt: Sonne.");
      return true;
    }

    plugin.messages().error(player, "Usage: /wetter <regen|gewitter|sonne>");
    return true;
  }
}
