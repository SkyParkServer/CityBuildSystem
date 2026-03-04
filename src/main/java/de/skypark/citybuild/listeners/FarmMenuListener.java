package de.skypark.citybuild.listeners;

import de.skypark.citybuild.core.FarmMenuHolder;
import de.skypark.citybuild.core.FarmMenuService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class FarmMenuListener implements Listener {

  private final FarmMenuService farmMenuService;

  public FarmMenuListener(FarmMenuService farmMenuService) {
    this.farmMenuService = farmMenuService;
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }
    if (!(event.getView().getTopInventory().getHolder() instanceof FarmMenuHolder holder)) {
      return;
    }

    event.setCancelled(true);

    if (event.getClickedInventory() == null) {
      return;
    }
    if (!event.getClickedInventory().equals(event.getView().getTopInventory())) {
      return;
    }

    int slot = event.getSlot();
    if (slot == holder.closeSlot()) {
      player.closeInventory();
      return;
    }

    String serverName = holder.serverAt(slot);
    if (serverName == null || serverName.isBlank()) {
      return;
    }

    player.closeInventory();
    farmMenuService.connect(player, serverName);
  }
}
