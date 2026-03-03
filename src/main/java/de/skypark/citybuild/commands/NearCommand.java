package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NearCommand implements CommandExecutor {

  private final CityBuildSystem plugin;

  public NearCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }
    if (!player.hasPermission("cb.near.use")) {
      plugin.messages().error(player, "Du hast dazu keine Rechte!");
      return true;
    }

    List<String> nearby = new ArrayList<>();
    Location base = player.getLocation();
    for (Player online : player.getWorld().getPlayers()) {
      if (online.getUniqueId().equals(player.getUniqueId())) {
        continue;
      }
      if (!plugin.vanishService().isVisibleTo(player, online)) {
        continue;
      }
      double distance = base.distance(online.getLocation());
      if (distance <= 200.0D) {
        nearby.add(online.getName() + " (&e" + (int) distance + "m&7)");
      }
    }

    nearby.sort(Comparator.naturalOrder());
    if (nearby.isEmpty()) {
      plugin.messages().message(player, "&7Es sind keine Spieler in deiner Naehe.");
      return true;
    }

    plugin.messages().message(player, "&7Spieler in der Naehe: &b" + String.join("&7, &b", nearby));
    return true;
  }
}
