package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.listeners.SharedEnderChestListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EnderChestCommand implements CommandExecutor {

    private final CityBuildSystem plugin;

    public EnderChestCommand(CityBuildSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
            return true;
        }

        if (!player.hasPermission("cb.enderchest.use")) {
            plugin.messages().error(player, "Du hast dazu keine Rechte!");
            return true;
        }

        Inventory inv = plugin.enderChestStore().load(player.getUniqueId());
        SharedEnderChestListener.markOpen(player.getUniqueId());
        player.openInventory(inv);
        return true;
    }
}
