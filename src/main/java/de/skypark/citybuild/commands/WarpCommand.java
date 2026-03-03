package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    private final CityBuildSystem plugin;

    public WarpCommand(CityBuildSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
            return true;
        }
        if (!player.hasPermission("cb.warp.warps")) {
            player.sendMessage(plugin.messages().color("§6§lSkyPark §8» §7Du hast dazu keine Rechte!"));
            return true;
        }
        if (args.length < 1) {
            plugin.messages().error(player, "Nutze: /warp <Name>");
            return true;
        }

        String name = args[0].toLowerCase();
        String serverName = plugin.getConfig().getString("server-name", "citybuild-1");
        Location target = plugin.warpStore().getWarp(serverName, name);
        if (target == null) {
            plugin.messages().error(player, "Warp nicht gefunden.");
            return true;
        }

        player.teleport(target);
        player.sendMessage(plugin.messages().color("§6§lSkyPark §8» §7Du hast dich zu den §b" + name + " §7Teleportiert"));
        return true;
    }
}
