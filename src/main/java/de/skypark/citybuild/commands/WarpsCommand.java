package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpsCommand implements CommandExecutor {

    private final CityBuildSystem plugin;

    public WarpsCommand(CityBuildSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
            return true;
        }
        if (!player.hasPermission("cb.warp.warps")) {
            plugin.messages().error(player, "Du hast dazu keine Rechte!");
            return true;
        }

        String serverName = plugin.getConfig().getString("server-name", "citybuild-1");
        List<String> warps = plugin.warpStore().listWarps(serverName);
        if (warps.isEmpty()) {
            plugin.messages().message(player, "&7Es sind keine Warps gesetzt.");
            return true;
        }

        plugin.messages().message(player, "&7Warps: &b" + String.join("&7, &b", warps));
        return true;
    }
}
