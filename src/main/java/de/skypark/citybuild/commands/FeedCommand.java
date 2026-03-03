package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor, TabCompleter {

  private final CityBuildSystem plugin;

  public FeedCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length >= 1) {
      if (!sender.hasPermission("cb.feed.other")) {
        plugin.messages().error(sender, "Du hast dazu keine Rechte!");
        return true;
      }
      Player target = Bukkit.getPlayerExact(args[0]);
      if (target == null) {
        plugin.messages().error(sender, "Spieler ist nicht online.");
        return true;
      }
      target.setFoodLevel(20);
      target.setSaturation(20F);
      plugin.messages().success(sender, "Du hast &e" + target.getName() + "&a gefuettert.");
      plugin.messages().success(target, "Du wurdest gefuettert.");
      return true;
    }

    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nutze: /feed <Spieler>");
      return true;
    }
    if (!player.hasPermission("cb.feed.use")) {
      plugin.messages().error(player, "Du hast dazu keine Rechte!");
      return true;
    }
    player.setFoodLevel(20);
    player.setSaturation(20F);
    plugin.messages().success(player, "Du bist jetzt satt.");
    return true;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String alias, String[] args) {
    if (args.length != 1) {
      return List.of();
    }
    String prefix = args[0].toLowerCase();
    List<String> names = new ArrayList<>();
    for (Player online : Bukkit.getOnlinePlayers()) {
      if (online.getName().toLowerCase().startsWith(prefix)) {
        names.add(online.getName());
      }
    }
    return names;
  }
}
