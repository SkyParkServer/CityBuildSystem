package de.skypark.citybuild.commands.framework;

import de.skypark.citybuild.CityBuildSystem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

  protected final CityBuildSystem plugin;

  protected AbstractCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  protected final Player requirePlayer(CommandSender sender) {
    if (sender instanceof Player player) {
      return player;
    }
    plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
    return null;
  }

  protected final boolean requirePermission(CommandSender sender, String permission) {
    if (sender.hasPermission(permission)) {
      return true;
    }
    plugin.messages().error(sender, "Du hast dazu keine Rechte!");
    return false;
  }

  protected final List<String> completeFirstArg(String[] args, Collection<String> values) {
    if (args.length != 1) {
      return List.of();
    }
    String prefix = args[0].toLowerCase();
    List<String> result = new ArrayList<>();
    for (String value : values) {
      if (value.toLowerCase().startsWith(prefix)) {
        result.add(value);
      }
    }
    return result;
  }

  protected final List<String> completeOnlinePlayers(String[] args) {
    if (args.length != 1) {
      return List.of();
    }
    return plugin.vanishService().visibleOnlineNames(null, args[0]);
  }

  protected final List<String> completeOnlinePlayers(CommandSender sender, String[] args) {
    if (args.length != 1) {
      return List.of();
    }
    return plugin.vanishService().visibleOnlineNames(sender, args[0]);
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String alias, String[] args) {
    return List.of();
  }
}
