package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.commands.framework.AbstractCommand;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand extends AbstractCommand {

  public HealCommand(CityBuildSystem plugin) {
    super(plugin);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length >= 1) {
      if (!sender.hasPermission("cb.heal.other.use")) {
        plugin.messages().error(sender, "Du hast dazu keine Rechte!");
        return true;
      }
      Player target = Bukkit.getPlayerExact(args[0]);
      if (target == null) {
        plugin.messages().error(sender, "Spieler ist nicht online.");
        return true;
      }
      heal(target);
      plugin.messages().success(sender, "Du hast &e" + target.getName() + "&a geheilt.");
      plugin.messages().success(target, "Du wurdest geheilt.");
      return true;
    }

    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nutze: /heal <Spieler>");
      return true;
    }
    if (!player.hasPermission("cb.heal.use")) {
      plugin.messages().error(player, "Du hast dazu keine Rechte!");
      return true;
    }

    heal(player);
    plugin.messages().success(player, "Du bist jetzt voll geheilt.");
    return true;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String alias, String[] args) {
    return completeOnlinePlayers(args);
  }

  private void heal(Player target) {
    target.setHealth(target.getMaxHealth());
    target.setFireTicks(0);
  }
}
