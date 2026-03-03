package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EcoCommand implements CommandExecutor {

  private final CityBuildSystem plugin;

  public EcoCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    // Usage: /eco <add|set|take> <player> <amount>
    if (args.length < 3) {
      plugin.messages().error(sender, "Usage: /eco <add|set|take> <player> <amount>");
      return true;
    }

    String action = args[0].toLowerCase();
    OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[1]);

    int amount;
    try {
      amount = Integer.parseInt(args[2]);
    } catch (NumberFormatException ex) {
      plugin.messages().error(sender, "Usage: /eco <add|set|take> <player> <amount>");
      return true;
    }

    if (action.equals("add")) {
      plugin.money().addMoney(target, amount);
      plugin
          .messages()
          .success(sender, "Added &6$" + amount + "&a to &e" + target.getName() + "&a.");
      return true;
    }

    if (action.equals("set")) {
      plugin.money().setMoney(target, amount);
      plugin
          .messages()
          .success(sender, "Set balance of &e" + target.getName() + "&a to &6$" + amount + "&a.");
      return true;
    }

    if (action.equals("take")) {
      if (!plugin.money().takeMoney(target, amount)) {
        plugin.messages().error(sender, "Player has not enough money.");
        return true;
      }
      plugin
          .messages()
          .success(sender, "Removed &6$" + amount + "&a from &e" + target.getName() + "&a.");
      return true;
    }

    plugin.messages().error(sender, "Usage: /eco <add|set|take> <player> <amount>");
    return true;
  }
}
