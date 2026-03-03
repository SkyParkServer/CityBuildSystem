package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

  private final CityBuildSystem plugin;

  public BalanceCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    OfflinePlayer target;

    if (args.length >= 1) {
      target = plugin.getServer().getOfflinePlayer(args[0]);

      if (sender instanceof Player player) {
        if (!player.hasPermission("citybuild.admin.economy")) {
          plugin.messages().error(player, "You cannot view other player balances.");
          return true;
        }
      }
    } else {
      if (!(sender instanceof Player player)) {
        plugin.messages().error(sender, "Usage: /balance <player>");
        return true;
      }
      target = player;
    }

    double balance = plugin.money().balance(target);
    plugin
        .messages()
        .message(sender, "&e" + target.getName() + "&7 has &6$" + (int) balance + "&7.");
    return true;
  }
}
