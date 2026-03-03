package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.storage.WerbungStore;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WerbungTpCommand implements CommandExecutor {

    private final CityBuildSystem plugin;
    private final WerbungStore werbungStore;

    public WerbungTpCommand(CityBuildSystem plugin, WerbungStore werbungStore) {
        this.plugin = plugin;
        this.werbungStore = werbungStore;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (args.length < 1) {
            plugin.messages().error(player, "Teleport-Ziel nicht gefunden.");
            return true;
        }

        UUID owner;
        try {
            owner = UUID.fromString(args[0]);
        } catch (IllegalArgumentException ex) {
            plugin.messages().error(player, "Teleport-Ziel nicht gefunden.");
            return true;
        }

        Location target = werbungStore.getTeleportTarget(owner);
        if (target == null) {
            plugin.messages().error(player, "Teleport-Ziel ist nicht verfuegbar.");
            return true;
        }

        player.teleport(target);
        plugin.messages().success(player, "Du wurdest zum Plot teleportiert.");
        return true;
    }
}
