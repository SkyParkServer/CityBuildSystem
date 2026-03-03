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
    if (args.length < 3) {
      plugin.messages().error(sender, "Nutze: /eco <add|set|take> <Spieler> <Betrag>");
      return true;
    }

    String action = args[0].toLowerCase();
    OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[1]);

    int amount;
    try {
      amount = Integer.parseInt(args[2]);
    } catch (NumberFormatException ex) {
      plugin.messages().error(sender, "Nutze: /eco <add|set|take> <Spieler> <Betrag>");
      return true;
    }

    if (action.equals("add")) {
      plugin.money().addMoney(target, amount);
      plugin
          .messages()
          .success(
              sender, "&6$" + amount + "&a wurden zu &e" + target.getName() + "&a hinzugefuegt.");
      return true;
    }

    if (action.equals("set")) {
      plugin.money().setMoney(target, amount);
      plugin
          .messages()
          .success(
              sender,
              "Der Kontostand von &e"
                  + target.getName()
                  + "&a wurde auf &6$"
                  + amount
                  + "&a gesetzt.");
      return true;
    }

    if (action.equals("take")) {
      if (!plugin.money().takeMoney(target, amount)) {
        plugin.messages().error(sender, "Der Spieler hat nicht genug Geld.");
        return true;
      }
      plugin
          .messages()
          .success(sender, "&6$" + amount + "&a wurden von &e" + target.getName() + "&a entfernt.");
      return true;
    }

    plugin.messages().error(sender, "Nutze: /eco <add|set|take> <Spieler> <Betrag>");
    return true;
  }
}
