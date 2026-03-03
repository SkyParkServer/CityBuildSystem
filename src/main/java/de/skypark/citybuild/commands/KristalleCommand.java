package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.storage.CrystalsStore;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KristalleCommand implements CommandExecutor {

  private final CityBuildSystem plugin;
  private final CrystalsStore crystals;

  public KristalleCommand(CityBuildSystem plugin, CrystalsStore crystals) {
    this.plugin = plugin;
    this.crystals = crystals;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!sender.hasPermission("cb.kristalle.admin")) {
      plugin.messages().error(sender, "Du hast dazu keine Rechte!");
      return true;
    }
    if (args.length < 1) {
      plugin
          .messages()
          .error(
              sender,
              "Nutze: /kristalle <Spieler> | /kristalle <give|set|reset> <Spieler> [Betrag]");
      return true;
    }

    if (args.length == 1) {
      OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
      plugin
          .messages()
          .message(
              sender, "&7Kristalle von &e" + target.getName() + "&7: &b" + crystals.get(target));
      return true;
    }

    String action = args[0].toLowerCase();
    OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[1]);

    if ("reset".equals(action)) {
      crystals.reset(target);
      plugin
          .messages()
          .success(sender, "Kristalle von &e" + target.getName() + "&a wurden zurueckgesetzt.");
      return true;
    }

    if (args.length < 3) {
      plugin.messages().error(sender, "Betrag fehlt.");
      return true;
    }

    int amount;
    try {
      amount = Integer.parseInt(args[2]);
    } catch (NumberFormatException ex) {
      plugin.messages().error(sender, "Ungueltiger Betrag.");
      return true;
    }

    if ("give".equals(action)) {
      crystals.add(target, amount);
      plugin
          .messages()
          .success(
              sender,
              "Es wurden &b" + amount + "&a Kristalle an &e" + target.getName() + "&a gegeben.");
      return true;
    }
    if ("set".equals(action)) {
      crystals.set(target, amount);
      plugin
          .messages()
          .success(
              sender,
              "Kristalle von &e"
                  + target.getName()
                  + "&a wurden auf &b"
                  + Math.max(0, amount)
                  + "&a gesetzt.");
      return true;
    }

    plugin
        .messages()
        .error(
            sender, "Nutze: /kristalle <Spieler> | /kristalle <give|set|reset> <Spieler> [Betrag]");
    return true;
  }
}
