package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgToggleCommand implements CommandExecutor {

  private final CityBuildSystem plugin;

  public MsgToggleCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }
    if (!player.hasPermission("cb.msg.msgtoggle")) {
      plugin.messages().error(player, "Du hast dazu keine Rechte!");
      return true;
    }

    boolean enabled = plugin.messagingStore().isEnabled(player.getUniqueId());
    plugin.messagingStore().setEnabled(player.getUniqueId(), !enabled);
    if (enabled) {
      plugin.messages().success(player, "Private Nachrichten sind jetzt deaktiviert.");
    } else {
      plugin.messages().success(player, "Private Nachrichten sind jetzt aktiviert.");
    }
    return true;
  }
}
