package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.UUID;

public class RegenbogenCommand implements CommandExecutor {

    private final CityBuildSystem plugin;

    public RegenbogenCommand(CityBuildSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
            return true;
        }
        if (!player.hasPermission("cb.regenbogen.use")) {
            plugin.messages().error(player, "Du hast dazu keine Rechte!");
            return true;
        }

        UUID uuid = player.getUniqueId();
        if (plugin.isRainbowArmorEnabled(uuid)) {
            plugin.setRainbowArmorEnabled(uuid, false);
            plugin.messages().success(player, "Regenbogen-Ruestung deaktiviert.");
            return true;
        }

        equipLeather(player);
        plugin.setRainbowArmorEnabled(uuid, true);
        plugin.messages().success(player, "Regenbogen-Ruestung aktiviert.");
        return true;
    }

    private void equipLeather(Player player) {
        player.getInventory().setHelmet(colored(Material.LEATHER_HELMET));
        player.getInventory().setChestplate(colored(Material.LEATHER_CHESTPLATE));
        player.getInventory().setLeggings(colored(Material.LEATHER_LEGGINGS));
        player.getInventory().setBoots(colored(Material.LEATHER_BOOTS));
    }

    private ItemStack colored(Material material) {
        ItemStack item = new ItemStack(material);
        if (item.getItemMeta() instanceof LeatherArmorMeta meta) {
            meta.setColor(Color.RED);
            item.setItemMeta(meta);
        }
        return item;
    }
}
