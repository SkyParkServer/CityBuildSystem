package de.skypark.citybuild.core;

import java.util.Map;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class FarmMenuHolder implements InventoryHolder {

  private final Map<Integer, String> serverBySlot;
  private final int closeSlot;
  private Inventory inventory;

  public FarmMenuHolder(Map<Integer, String> serverBySlot, int closeSlot) {
    this.serverBySlot = serverBySlot;
    this.closeSlot = closeSlot;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  public int closeSlot() {
    return closeSlot;
  }

  public String serverAt(int slot) {
    return serverBySlot.get(slot);
  }

  @Override
  public Inventory getInventory() {
    return inventory;
  }
}
