package de.skypark.citybuild.listeners;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SharedEnderChestListener implements Listener {

    private static final Set<UUID> OPENED = new HashSet<>();

    private final CityBuildSystem plugin;

    public SharedEnderChestListener(CityBuildSystem plugin) {
        this.plugin = plugin;
    }

    public static void markOpen(UUID uuid) {
        OPENED.add(uuid);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        UUID uuid = player.getUniqueId();
        if (!OPENED.remove(uuid)) {
            return;
        }

        plugin.enderChestStore().save(uuid, event.getInventory());
    }
}
