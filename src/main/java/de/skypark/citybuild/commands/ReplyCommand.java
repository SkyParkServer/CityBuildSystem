package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {

  private final CityBuildSystem plugin;

  public ReplyCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }
    if (!player.hasPermission("cb.msg.reply.use")) {
      plugin.messages().error(player, "Du hast dazu keine Rechte!");
      return true;
    }
    if (args.length < 1) {
      plugin.messages().error(player, "Nutze: /r <Nachricht>");
      return true;
    }

    UUID targetUuid = plugin.messagingStore().lastReplyTarget(player.getUniqueId());
    if (targetUuid == null) {
      plugin.messages().error(player, "Du hast keine letzte Konversation.");
      return true;
    }

    if (!plugin.messagingStore().isEnabled(targetUuid)) {
      plugin.messages().error(player, "Dieser Spieler hat private Nachrichten deaktiviert.");
      return true;
    }

    String msg = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
    Player onlineTarget = Bukkit.getPlayer(targetUuid);
    String targetName = plugin.playerLookup().findNameByUuid(targetUuid);

    player.sendMessage(
        plugin.messages().color("&8[&6MSG&8] &7Du -> &b" + targetName + "&8: &f" + msg));
    if (onlineTarget != null) {
      onlineTarget.sendMessage(
          plugin.messages().color("&8[&6MSG&8] &b" + player.getName() + " &7-> Du&8: &f" + msg));
    } else {
      plugin.messagingStore().queueMessage(targetUuid, player.getUniqueId(), player.getName(), msg);
    }

    plugin.messagingStore().updateLast(player.getUniqueId(), targetUuid);
    return true;
  }
}
