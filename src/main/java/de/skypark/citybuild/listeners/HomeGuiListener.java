package de.skypark.citybuild.listeners;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.core.HomeService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HomeGuiListener implements Listener {

  private final CityBuildSystem plugin;
  private final HomeService homes;

  public HomeGuiListener(CityBuildSystem plugin, HomeService homes) {
    this.plugin = plugin;
    this.homes = homes;
  }

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) return;
    String title = event.getView().getTitle();

    // Main menu
    if (title.equals(homes.mainTitle())) {
      event.setCancelled(true);
      if (event.getClickedInventory() == null) return;
      if (event.getClickedInventory().equals(player.getInventory())) return;
      if (event.getSlot() < 0) return;

      int slot = event.getSlot();

      if (slot == 49) {
        player.closeInventory();
        homes.clearGuiState(player);
        return;
      }

      if (slot == 53) {
        homes.openBuyMenu(player);
        return;
      }

      String homeName = homes.getGuiHomeAt(player, slot);
      if (homeName != null && !homeName.isEmpty()) {
        if (event.getClick() == ClickType.RIGHT) {
          if (!player.hasPermission("cb.home.del.use")) {
            plugin.messages().error(player, "Du hast keine Rechte, um Homes zu loeschen.");
            return;
          }
          homes.openDeleteMenu(player, homeName);
          return;
        }

        if (!player.hasPermission("cb.home.home")) {
          plugin.messages().error(player, "Du hast keine Rechte, um zu Homes zu teleportieren.");
          return;
        }

        player.closeInventory();
        homes.teleportTo(player, homeName);
        return;
      }

      Integer freeIndex = homes.getGuiFreeAt(player, slot);
      if (freeIndex != null && freeIndex > 0) {
        plugin
            .messages()
            .message(
                player, "&7Nutze &e/sethome home" + freeIndex + " &7oder &e/sethome <name>&7.");
        return;
      }

      Integer lockedIndex = homes.getGuiLockedAt(player, slot);
      if (lockedIndex != null && lockedIndex > 0) {
        homes.openBuyMenu(player);
        return;
      }
    }

    // Delete menu
    if (title.equals(homes.deleteTitle())) {
      event.setCancelled(true);
      if (event.getClickedInventory() == null) return;
      if (event.getClickedInventory().equals(player.getInventory())) return;

      int slot = event.getSlot();
      if (slot == 40 || slot == 20) {
        homes.openMainMenu(player);
        return;
      }

      if (slot == 24) {
        String homeName = homes.getDeleteTarget(player);
        if (homeName != null && !homeName.isEmpty()) {
          homes.deleteHome(player, homeName);
          homes.refreshNameCache(player);
          plugin.messages().success(player, "Home &e" + homeName + "&a wurde geloescht.");
        }
        homes.openMainMenu(player);
        return;
      }
    }

    // Buy menu
    if (title.equals(homes.buyTitle())) {
      event.setCancelled(true);
      if (event.getClickedInventory() == null) return;
      if (event.getClickedInventory().equals(player.getInventory())) return;

      int slot = event.getSlot();
      if (slot == 49) {
        homes.openMainMenu(player);
        return;
      }
      if (slot != 25) return;

      int purchased = homes.purchased(player);
      int maxBuyable = homes.maxBuyable(player);
      if (purchased >= maxBuyable) {
        plugin.messages().error(player, "Du hast dein Kauf-Limit fuer weitere Homes erreicht.");
        homes.openBuyMenu(player);
        return;
      }

      int price = homes.nextPrice(player);
      if (plugin.money().balance(player) < price) {
        plugin.messages().error(player, "Du brauchst &6$" + price + "&c fuer das naechste Home.");
        homes.openBuyMenu(player);
        return;
      }

      if (!plugin.money().takeMoney(player, price)) {
        plugin.messages().error(player, "Bezahlung fehlgeschlagen.");
        homes.openBuyMenu(player);
        return;
      }

      homes.setPurchased(player, purchased + 1);
      plugin
          .messages()
          .success(player, "Du hast 1 zusaetzlichen Home-Slot fuer &6$" + price + "&a gekauft.");
      homes.openMainMenu(player);
    }
  }
}
