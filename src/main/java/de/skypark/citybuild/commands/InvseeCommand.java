package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.commands.framework.AbstractCommand;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand extends AbstractCommand {

  public InvseeCommand(CityBuildSystem plugin) {
    super(plugin);
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

    Player target = plugin.vanishService().findVisiblePlayer(player, args[0]);
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
    return completeOnlinePlayers(sender, args);
  }
}
