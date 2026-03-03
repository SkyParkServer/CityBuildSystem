package de.skypark.citybuild.core;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.storage.TresorStore;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TresorService {

  public static final int MAX_PAGES = 8;
  public static final int PREVIOUS_SLOT = 48;
  public static final int CLOSE_SLOT = 49;
  public static final int NEXT_SLOT = 50;

  private static final long OPEN_COOLDOWN_MS = 10_000L;

  private static final int[] STORAGE_SLOTS = {
    10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39,
    40, 41, 42, 43
  };

  private static final Set<Integer> STORAGE_SLOT_SET = new HashSet<>();

  static {
    Arrays.stream(STORAGE_SLOTS).forEach(STORAGE_SLOT_SET::add);
  }

  private final CityBuildSystem plugin;
  private final TresorStore store;
  private final java.util.Map<UUID, Long> openCooldownUntil = new java.util.HashMap<>();

  public TresorService(CityBuildSystem plugin, TresorStore store) {
    this.plugin = plugin;
    this.store = store;
  }

  public void openFromCommand(Player player) {
    long now = System.currentTimeMillis();
    long availableAt = openCooldownUntil.getOrDefault(player.getUniqueId(), 0L);
    if (now < availableAt) {
      int seconds = (int) Math.ceil((availableAt - now) / 1000.0);
      plugin
          .messages()
          .error(
              player, "Du kannst den Tresor erst in &e" + seconds + " &cSekunden wieder oeffnen.");
      return;
    }

    openCooldownUntil.put(player.getUniqueId(), now + OPEN_COOLDOWN_MS);

    int startPage = firstUnlockedPage(player);
    openPage(player, startPage);
    if (!hasPagePermission(player, startPage)) {
      plugin
          .messages()
          .error(
              player,
              "Du hast noch keine Tresor-Seite freigeschaltet."
                  + " Benoetigt: &ecb.tresor.seite.1");
    }
  }

  public void openPage(Player player, int page) {
    int normalizedPage = normalizePage(page);
    boolean unlocked = hasPagePermission(player, normalizedPage);

    Inventory inv =
        Bukkit.createInventory(
            new TresorInventoryHolder(normalizedPage, unlocked), 54, titleForPage(normalizedPage));

    paintStaticLayout(inv, normalizedPage);

    if (unlocked) {
      ItemStack[] saved =
          store.loadPage(player.getUniqueId(), normalizedPage, STORAGE_SLOTS.length);
      for (int i = 0; i < STORAGE_SLOTS.length; i++) {
        inv.setItem(STORAGE_SLOTS[i], saved[i]);
      }
    } else {
      ItemStack lockedPane =
          named(
              Material.RED_STAINED_GLASS_PANE,
              "&cSeite nicht freigeschaltet",
              java.util.List.of(
                  "", "&7Benoetigte Permission:", "&e" + pagePermission(normalizedPage)));
      for (int slot : STORAGE_SLOTS) {
        inv.setItem(slot, lockedPane);
      }
    }

    player.openInventory(inv);
  }

  public void openRelativePage(Player player, int currentPage, int delta) {
    int next = normalizePage(currentPage + delta);
    openPage(player, next);
  }

  public void savePage(Player player, int page, boolean unlocked, Inventory inventory) {
    if (!unlocked) {
      return;
    }

    ItemStack[] data = new ItemStack[STORAGE_SLOTS.length];
    for (int i = 0; i < STORAGE_SLOTS.length; i++) {
      data[i] = inventory.getItem(STORAGE_SLOTS[i]);
    }
    store.savePage(player.getUniqueId(), normalizePage(page), data);
  }

  public boolean isStorageSlot(int slot) {
    return STORAGE_SLOT_SET.contains(slot);
  }

  public int normalizePage(int page) {
    if (page < 1) {
      return MAX_PAGES;
    }
    if (page > MAX_PAGES) {
      return 1;
    }
    return page;
  }

  public String pagePermission(int page) {
    return "cb.tresor.seite." + normalizePage(page);
  }

  public boolean hasPagePermission(Player player, int page) {
    return player.hasPermission(pagePermission(page));
  }

  private int firstUnlockedPage(Player player) {
    for (int page = 1; page <= MAX_PAGES; page++) {
      if (hasPagePermission(player, page)) {
        return page;
      }
    }
    return 1;
  }

  private String titleForPage(int page) {
    return plugin.messages().color("&6&lTresor &8| &7Seite &e" + page + "&7/&e" + MAX_PAGES);
  }

  private void paintStaticLayout(Inventory inv, int page) {
    ItemStack border = named(Material.ORANGE_STAINED_GLASS_PANE, "&6 ", null);
    int[] borderSlots = {
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 51, 52, 53
    };
    for (int slot : borderSlots) {
      inv.setItem(slot, border);
    }

    inv.setItem(
        PREVIOUS_SLOT,
        named(
            Material.ARROW,
            "&eVorherige Seite",
            java.util.List.of("", "&7Zur Seite &e" + normalizePage(page - 1) + " &7wechseln")));

    inv.setItem(CLOSE_SLOT, named(Material.BARRIER, "&cSchliessen", null));

    inv.setItem(
        NEXT_SLOT,
        named(
            Material.ARROW,
            "&eNaechste Seite",
            java.util.List.of("", "&7Zur Seite &e" + normalizePage(page + 1) + " &7wechseln")));
  }

  private ItemStack named(Material material, String name, java.util.List<String> lore) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(plugin.messages().color(name));
      if (lore != null) {
        meta.setLore(lore.stream().map(plugin.messages()::color).toList());
      }
      item.setItemMeta(meta);
    }
    return item;
  }

  public static class TresorInventoryHolder implements InventoryHolder {

    private final int page;
    private final boolean unlocked;

    public TresorInventoryHolder(int page, boolean unlocked) {
      this.page = page;
      this.unlocked = unlocked;
    }

    public int page() {
      return page;
    }

    public boolean unlocked() {
      return unlocked;
    }

    @Override
    public Inventory getInventory() {
      return null;
    }
  }
}
