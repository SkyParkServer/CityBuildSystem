package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.commands.framework.AbstractCommand;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgCommand extends AbstractCommand {

  public MsgCommand(CityBuildSystem plugin) {
    super(plugin);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }
    if (!player.hasPermission("cb.msg.use")) {
      plugin.messages().error(player, "Du hast dazu keine Rechte!");
      return true;
    }
    if (args.length < 2) {
      plugin.messages().error(player, "Nutze: /msg <Spieler> <Nachricht>");
      return true;
    }

    String targetName = args[0];
    UUID targetUuid = plugin.playerLookup().findUuidByName(targetName);
    if (targetUuid == null) {
      plugin.messages().error(player, "Spieler nicht gefunden.");
      return true;
    }
    if (targetUuid.equals(player.getUniqueId())) {
      plugin.messages().error(player, "Du kannst dir selbst keine Nachricht senden.");
      return true;
    }
    if (!plugin.messagingStore().isEnabled(targetUuid)) {
      plugin.messages().error(player, "Dieser Spieler hat private Nachrichten deaktiviert.");
      return true;
    }

    String msg = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
    Player onlineTarget = Bukkit.getPlayer(targetUuid);

    player.sendMessage(
        plugin
            .messages()
            .color(
                "&8[&6MSG&8] &7Du -> &b"
                    + plugin.playerLookup().findNameByUuid(targetUuid)
                    + "&8: &f"
                    + msg));
    if (onlineTarget != null) {
      onlineTarget.sendMessage(
          plugin.messages().color("&8[&6MSG&8] &b" + player.getName() + " &7-> Du&8: &f" + msg));
    } else {
      plugin.messagingStore().queueMessage(targetUuid, player.getUniqueId(), player.getName(), msg);
    }

    plugin.messagingStore().updateLast(player.getUniqueId(), targetUuid);
    return true;
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String alias, String[] args) {
    return completeOnlinePlayers(sender, args);
  }
}
