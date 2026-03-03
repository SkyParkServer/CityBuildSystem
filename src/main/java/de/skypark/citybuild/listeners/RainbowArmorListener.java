package de.skypark.citybuild.listeners;

import de.skypark.citybuild.CityBuildSystem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class RainbowArmorListener implements Listener {

  private static final Set<UUID> ENABLED = new HashSet<>();
  private static final Map<UUID, ItemStack[]> BACKUP = new HashMap<>();
  private static final Color[] COLORS =
      new Color[] {
        Color.RED, Color.ORANGE, Color.YELLOW, Color.LIME,
        Color.AQUA, Color.BLUE, Color.PURPLE, Color.FUCHSIA
      };
  private static int colorIndex = 0;

  private final CityBuildSystem plugin;

  public RainbowArmorListener(CityBuildSystem plugin) {
    this.plugin = plugin;
    Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 2L, 10L);
  }

  public static void setEnabled(UUID uuid, boolean enabled) {
    if (enabled) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null && !BACKUP.containsKey(uuid)) {
        BACKUP.put(uuid, player.getInventory().getArmorContents());
      }
      ENABLED.add(uuid);
      return;
    }

    ENABLED.remove(uuid);
    Player player = Bukkit.getPlayer(uuid);
    ItemStack[] backup = BACKUP.remove(uuid);
    if (player != null) {
      if (backup != null) {
        player.getInventory().setArmorContents(backup);
      } else {
        player.getInventory().setArmorContents(new ItemStack[] {null, null, null, null});
      }
    }
  }

  public static boolean isEnabled(UUID uuid) {
    return ENABLED.contains(uuid);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    if (ENABLED.contains(uuid)) {
      setEnabled(uuid, false);
    }
  }

  @EventHandler
  public void onArmorMove(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }
    if (!ENABLED.contains(player.getUniqueId())) {
      return;
    }

    int rawSlot = event.getRawSlot();
    if (rawSlot >= 5 && rawSlot <= 8) {
      event.setCancelled(true);
    }
  }

  private void tick() {
    colorIndex = (colorIndex + 1) % COLORS.length;
    Color color = COLORS[colorIndex];

    for (UUID uuid : Set.copyOf(ENABLED)) {
      Player player = Bukkit.getPlayer(uuid);
      if (player == null) {
        continue;
      }
      apply(player, Material.LEATHER_HELMET, 3, color);
      apply(player, Material.LEATHER_CHESTPLATE, 2, color);
      apply(player, Material.LEATHER_LEGGINGS, 1, color);
      apply(player, Material.LEATHER_BOOTS, 0, color);
    }
  }

  private void apply(Player player, Material material, int armorIndex, Color color) {
    ItemStack[] armor = player.getInventory().getArmorContents();
    ItemStack piece = armor.length > armorIndex ? armor[armorIndex] : null;
    if (piece == null || piece.getType() != material) {
      piece = new ItemStack(material);
    }
    if (piece.getItemMeta() instanceof LeatherArmorMeta meta) {
      meta.setColor(color);
      piece.setItemMeta(meta);
    }
    armor[armorIndex] = piece;
    player.getInventory().setArmorContents(armor);
  }
}
