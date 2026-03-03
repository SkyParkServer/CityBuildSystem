package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.commands.framework.AbstractCommand;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand extends AbstractCommand {

  public FeedCommand(CityBuildSystem plugin) {
    super(plugin);
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
    return completeOnlinePlayers(args);
  }
}
