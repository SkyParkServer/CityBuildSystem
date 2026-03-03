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

public class InvseeCommand implements CommandExecutor, TabCompleter {

  private final CityBuildSystem plugin;

  public InvseeCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }
    if (!player.hasPermission("cb.invsee.use")) {
      plugin.messages().error(player, "Du hast dazu keine Rechte!");
      return true;
    }
    if (args.length < 1) {
      plugin.messages().error(player, "Nutze: /invsee <Spieler>");
      return true;
    }

    Player target = Bukkit.getPlayerExact(args[0]);
    if (target == null) {
      plugin.messages().error(player, "Spieler ist nicht online.");
      return true;
    }
    if (target.getUniqueId().equals(player.getUniqueId())) {
      plugin.messages().error(player, "Du kannst dein eigenes Inventar nicht mit /invsee oeffnen.");
      return true;
    }

    player.openInventory(target.getInventory());
    if (!player.hasPermission("cb.invsee.admin")) {
      plugin.registerInvseeViewer(player.getUniqueId(), target.getUniqueId());
    }
    plugin
        .messages()
        .success(player, "Du siehst nun das Inventar von &e" + target.getName() + "&a.");
    return true;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String alias, String[] args) {
    if (args.length != 1) {
      return List.of();
    }
    List<String> result = new ArrayList<>();
    String prefix = args[0].toLowerCase();
    for (Player online : Bukkit.getOnlinePlayers()) {
      if (online.getName().toLowerCase().startsWith(prefix)) {
        result.add(online.getName());
      }
    }
    return result;
  }
}
