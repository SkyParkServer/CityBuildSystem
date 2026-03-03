package de.skypark.citybuild.listeners;

import de.skypark.citybuild.core.TresorService;
import de.skypark.citybuild.core.TresorService.TresorInventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class TresorListener implements Listener {

  private final TresorService tresorService;

  public TresorListener(TresorService tresorService) {
    this.tresorService = tresorService;
  }

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }

    Inventory top = event.getView().getTopInventory();
    if (!(top.getHolder() instanceof TresorInventoryHolder holder)) {
      return;
    }

    if (event.getClick() == ClickType.DOUBLE_CLICK) {
      event.setCancelled(true);
      return;
    }

    int rawSlot = event.getRawSlot();
    if (rawSlot < 0) {
      return;
    }

    int topSize = top.getSize();
    if (rawSlot >= topSize) {
      return;
    }

    event.setCancelled(true);

    if (rawSlot == TresorService.CLOSE_SLOT) {
      player.closeInventory();
      return;
    }

    if (rawSlot == TresorService.PREVIOUS_SLOT) {
      tresorService.savePage(player, holder.page(), holder.unlocked(), top);
      tresorService.openRelativePage(player, holder.page(), -1);
      return;
    }

    if (rawSlot == TresorService.NEXT_SLOT) {
      tresorService.savePage(player, holder.page(), holder.unlocked(), top);
      tresorService.openRelativePage(player, holder.page(), 1);
      return;
    }

    if (!holder.unlocked()) {
      return;
    }

    if (tresorService.isStorageSlot(rawSlot)) {
      event.setCancelled(false);
    }
  }

  @EventHandler
  public void onDrag(InventoryDragEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }

    Inventory top = event.getView().getTopInventory();
    if (!(top.getHolder() instanceof TresorInventoryHolder holder)) {
      return;
    }

    int topSize = top.getSize();
    for (int slot : event.getRawSlots()) {
      if (slot >= topSize) {
        continue;
      }
      if (!holder.unlocked() || !tresorService.isStorageSlot(slot)) {
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler
  public void onClose(InventoryCloseEvent event) {
    if (!(event.getPlayer() instanceof Player player)) {
      return;
    }

    Inventory top = event.getView().getTopInventory();
    if (!(top.getHolder() instanceof TresorInventoryHolder holder)) {
      return;
    }

    tresorService.savePage(player, holder.page(), holder.unlocked(), top);
  }
}
