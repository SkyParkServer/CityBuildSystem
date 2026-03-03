package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.commands.framework.AbstractCommand;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WetterCommand extends AbstractCommand {

  private static final List<String> MODES = List.of("regen", "gewitter", "sonne");

  public WetterCommand(CityBuildSystem plugin) {
    super(plugin);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Player player = requirePlayer(sender);
    if (player == null) {
      return true;
    }

    if (!requirePermission(player, "cb.wetter.use")) {
      return true;
    }

    if (args.length < 1) {
      plugin.messages().error(player, "Nutze: /wetter <regen|gewitter|sonne>");
      return true;
    }

    String mode = args[0].toLowerCase();
    if (mode.equals("regen")) {
      player.getWorld().setStorm(true);
      player.getWorld().setThundering(false);
      plugin.messages().success(player, "Wetter gesetzt: Regen.");
      return true;
    }
    if (mode.equals("gewitter")) {
      player.getWorld().setStorm(true);
      player.getWorld().setThundering(true);
      plugin.messages().success(player, "Wetter gesetzt: Gewitter.");
      return true;
    }
    if (mode.equals("sonne")) {
      player.getWorld().setStorm(false);
      player.getWorld().setThundering(false);
      plugin.messages().success(player, "Wetter gesetzt: Sonne.");
      return true;
    }

    plugin.messages().error(player, "Nutze: /wetter <regen|gewitter|sonne>");
    return true;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String alias, String[] args) {
    return completeFirstArg(args, MODES);
  }
}
