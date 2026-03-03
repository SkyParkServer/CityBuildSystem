package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.commands.framework.AbstractCommand;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand extends AbstractCommand {

  public VanishCommand(CityBuildSystem plugin) {
    super(plugin);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!sender.hasPermission("ch.vanish.use")) {
      plugin.messages().error(sender, "Du hast dazu keine Rechte!");
      return true;
    }

    if (args.length == 0) {
      if (!(sender instanceof Player player)) {
        plugin.messages().error(sender, "Nutze: /vanish <Spieler>");
        return true;
      }

      boolean nowVanished = plugin.vanishService().toggle(player);
      if (nowVanished) {
        plugin.messages().success(player, "Vanish wurde aktiviert.");
      } else {
        plugin.messages().success(player, "Vanish wurde deaktiviert.");
      }
      return true;
    }

    Player target = plugin.vanishService().findVisiblePlayer(sender, args[0]);
    if (target == null) {
      plugin.messages().error(sender, "Spieler ist nicht online.");
      return true;
    }

    plugin.vanishService().setVanished(target, true);
    plugin.messages().success(sender, "Spieler &e" + target.getName() + "&a ist jetzt im Vanish.");
    if (!(sender instanceof Player player) || !target.getUniqueId().equals(player.getUniqueId())) {
      plugin.messages().success(target, "Du wurdest in den Vanish gesetzt.");
    }
    return true;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String alias, String[] args) {
    return completeOnlinePlayers(sender, args);
  }
}
