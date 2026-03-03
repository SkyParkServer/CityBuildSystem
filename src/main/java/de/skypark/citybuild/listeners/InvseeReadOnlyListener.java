package de.skypark.citybuild.listeners;

import de.skypark.citybuild.CityBuildSystem;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InvseeReadOnlyListener implements Listener {

  private static final Map<UUID, UUID> VIEWING = new HashMap<>();
  private final CityBuildSystem plugin;

  public InvseeReadOnlyListener(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  public static void setViewing(UUID viewer, UUID target) {
    VIEWING.put(viewer, target);
  }

  public static void clearViewing(UUID viewer) {
    VIEWING.remove(viewer);
  }

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }
    UUID target = VIEWING.get(player.getUniqueId());
    if (target == null) {
      return;
    }
    if (player.hasPermission("cb.invsee.admin")) {
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void onDrag(InventoryDragEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }
    if (VIEWING.containsKey(player.getUniqueId()) && !player.hasPermission("cb.invsee.admin")) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onClose(InventoryCloseEvent event) {
    if (!(event.getPlayer() instanceof Player player)) {
      return;
    }
    if (VIEWING.remove(player.getUniqueId()) != null) {
      plugin.clearInvseeViewer(player.getUniqueId());
    }
  }
}
