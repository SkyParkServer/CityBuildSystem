package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MsgCommand implements CommandExecutor, TabCompleter {

    private final CityBuildSystem plugin;

    public MsgCommand(CityBuildSystem plugin) {
        this.plugin = plugin;
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

        player.sendMessage(plugin.messages().color("&8[&6MSG&8] &7Du -> &b" + plugin.playerLookup().findNameByUuid(targetUuid) + "&8: &f" + msg));
        if (onlineTarget != null) {
            onlineTarget.sendMessage(plugin.messages().color("&8[&6MSG&8] &b" + player.getName() + " &7-> Du&8: &f" + msg));
        } else {
            plugin.messagingStore().queueMessage(targetUuid, player.getUniqueId(), player.getName(), msg);
        }

        plugin.messagingStore().updateLast(player.getUniqueId(), targetUuid);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return List.of();
        }
        List<String> suggestions = new ArrayList<>();
        String prefix = args[0].toLowerCase();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getName().toLowerCase().startsWith(prefix)) {
                suggestions.add(online.getName());
            }
        }
        return suggestions;
    }
}
