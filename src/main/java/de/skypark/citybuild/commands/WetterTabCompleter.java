package de.skypark.citybuild.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class WetterTabCompleter implements TabCompleter {
  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String alias, String[] args) {
    if (args.length == 1) {
      return List.of("regen", "gewitter", "sonne");
    }
    return List.of();
  }
}
