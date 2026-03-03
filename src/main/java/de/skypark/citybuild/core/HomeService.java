package de.skypark.citybuild.core;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class HomeService {

    private final CityBuildSystem plugin;
    private final HomeConfig config;

    private final Map<UUID, Map<Integer, String>> guiSlotHome = new HashMap<>();
    private final Map<UUID, Map<Integer, Integer>> guiSlotFree = new HashMap<>();
    private final Map<UUID, Map<Integer, Integer>> guiSlotLocked = new HashMap<>();
    private final Map<UUID, String> deleteTarget = new HashMap<>();

    public HomeService(CityBuildSystem plugin, HomeConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public String mainTitle() { return plugin.messages().color("&6&lHomes"); }
    public String deleteTitle() { return plugin.messages().color("&c&lHome loeschen"); }
    public String buyTitle() { return plugin.messages().color("&6&lHomes kaufen"); }

    public int bedSlot(int index) {
        int[] slots = new int[]{11,12,13,14,15,20,21,22,23,24,29,30,31,32,33};
        if (index < 1 || index > slots.length) return -1;
        return slots[index-1];
    }

    public boolean isNameValid(String homeName) {
        if (homeName == null) return false;
        if (homeName.isEmpty()) return false;
        if (homeName.length() > 16) return false;
        return !(homeName.contains(" ")
                || homeName.contains(".")
                || homeName.contains("/")
                || homeName.contains("\\")
                || homeName.contains(":")
                || homeName.contains(";"));
    }

    public String serverName() { return config.serverName(); }
    public int maxTotal() { return config.maxTotalHomes(); }

    public int rankLimit(Player target) {
        int best = config.defaultRankHomes();
        for (String key : config.rankKeys()) {
            if (key.equalsIgnoreCase("default")) continue;
            int configured = config.homesForRankKey(key);
            if (configured <= best) continue;

            if (target.hasPermission("group." + key)
                    || target.hasPermission("cb.home.rank." + key)
                    || target.hasPermission(key)) {
                best = configured;
            }
        }
        if (best > maxTotal()) best = maxTotal();
        return best;
    }

    public int purchased(Player target) {
        String uuid = target.getUniqueId().toString();
        try (Connection c = plugin.data().db().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT purchased_homes FROM cb_players WHERE uuid = ?")) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setPurchased(Player target, int amount) {
        amount = Math.max(0, amount);
        plugin.playerData().ensureFirstJoin(target);
        String uuid = target.getUniqueId().toString();
        try (Connection c = plugin.data().db().getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE cb_players SET purchased_homes = ? WHERE uuid = ?")) {
            ps.setInt(1, amount);
            ps.setString(2, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int maxBuyable(Player target) {
        return Math.max(0, maxTotal() - rankLimit(target));
    }

    public int totalLimit(Player target) {
        int total = rankLimit(target) + purchased(target);
        return Math.min(total, maxTotal());
    }

    public int nextPrice(Player target) {
        return config.basePrice() + (purchased(target) * config.stepPrice());
    }

    public int count(Player target) {
        String uuid = target.getUniqueId().toString();
        try (Connection c = plugin.data().db().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM cb_homes WHERE uuid = ? AND server_name = ?")) {
            ps.setString(1, uuid);
            ps.setString(2, serverName());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean exists(Player target, String homeName) {
        String uuid = target.getUniqueId().toString();
        try (Connection c = plugin.data().db().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM cb_homes WHERE uuid=? AND server_name=? AND home_name=? LIMIT 1")) {
            ps.setString(1, uuid);
            ps.setString(2, serverName());
            ps.setString(3, homeName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveHome(Player target, String homeName) {
        Location loc = target.getLocation();
        String uuid = target.getUniqueId().toString();
        long now = System.currentTimeMillis();
        try (Connection c = plugin.data().db().getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO cb_homes (uuid,server_name,home_name,world_name,x,y,z,yaw,pitch,updated_at) VALUES (?,?,?,?,?,?,?,?,?,?) " +
                             "ON DUPLICATE KEY UPDATE world_name=VALUES(world_name),x=VALUES(x),y=VALUES(y),z=VALUES(z),yaw=VALUES(yaw),pitch=VALUES(pitch),updated_at=VALUES(updated_at)")) {
            ps.setString(1, uuid);
            ps.setString(2, serverName());
            ps.setString(3, homeName);
            ps.setString(4, loc.getWorld().getName());
            ps.setDouble(5, loc.getX());
            ps.setDouble(6, loc.getY());
            ps.setDouble(7, loc.getZ());
            ps.setFloat(8, loc.getYaw());
            ps.setFloat(9, loc.getPitch());
            ps.setLong(10, now);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteHome(Player target, String homeName) {
        String uuid = target.getUniqueId().toString();
        try (Connection c = plugin.data().db().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM cb_homes WHERE uuid=? AND server_name=? AND home_name=?")) {
            ps.setString(1, uuid);
            ps.setString(2, serverName());
            ps.setString(3, homeName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> listNames(Player target) {
        String uuid = target.getUniqueId().toString();
        List<String> names = new ArrayList<>();
        try (Connection c = plugin.data().db().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT home_name FROM cb_homes WHERE uuid=? AND server_name=? ORDER BY home_name ASC")) {
            ps.setString(1, uuid);
            ps.setString(2, serverName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) names.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    public void teleportTo(Player target, String homeName) {
        String uuid = target.getUniqueId().toString();
        try (Connection c = plugin.data().db().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT world_name,x,y,z,yaw,pitch FROM cb_homes WHERE uuid=? AND server_name=? AND home_name=?")) {
            ps.setString(1, uuid);
            ps.setString(2, serverName());
            ps.setString(3, homeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    plugin.messages().error(target, "Home &e" + homeName + "&c does not exist.");
                    return;
                }
                String worldName = rs.getString(1);
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.messages().error(target, "World &e" + worldName + "&c is not loaded.");
                    return;
                }
                Location loc = new Location(world, rs.getDouble(2), rs.getDouble(3), rs.getDouble(4), rs.getFloat(5), rs.getFloat(6));
                target.teleport(loc);
                plugin.messages().success(target, "Teleported to home &e" + homeName + "&a.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ItemStack named(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(plugin.messages().color(name));
        if (lore != null) meta.setLore(lore.stream().map(plugin.messages()::color).collect(Collectors.toList()));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack orangePane() { return named(Material.ORANGE_STAINED_GLASS_PANE, "&6 ", null); }

    public void openMainMenu(Player target) {
        List<String> names = listNames(target);

        int rankHomes = rankLimit(target);
        int purchased = purchased(target);
        int maxBuyable = maxBuyable(target);
        int totalLimit = totalLimit(target);

        Inventory inv = Bukkit.createInventory(null, 6*9, mainTitle());
        int[] border = new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,40,41,42,43,44,45,46,47,48,50,51,52};
        for (int s : border) inv.setItem(s, orangePane());
        inv.setItem(49, named(Material.BARRIER, "&cSchliessen", null));

        ItemStack info = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta im = info.getItemMeta();
        im.setDisplayName(plugin.messages().color("&b&lInfo"));
        int setCount = names.size();
        int remaining = Math.max(0, totalLimit - setCount);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("&7Gesetzte Homes: &b" + setCount + "&7/&b" + totalLimit);
        lore.add("&7Freie Slots: &a" + remaining);
        lore.add("&7Rang Homes: &b" + rankHomes);
        lore.add("&7Gekaufte Homes: &b" + purchased);
        lore.add("");
        lore.add("&7Tipp: Linksklick = Teleport");
        lore.add("&7Tipp: Rechtsklick = Loeschen");
        im.setLore(lore.stream().map(plugin.messages()::color).collect(Collectors.toList()));
        info.setItemMeta(im);
        inv.setItem(0, info);

        ItemStack buyPaper;
        if (purchased >= maxBuyable) {
            buyPaper = named(Material.PAPER, "&6&lHomes Kaufen",
                    List.of("", "&7Gekauft: &b" + purchased + "&7/&b" + maxBuyable, "&cDu hast bereits alle kaufbaren Slots.", "", "&7Klicke fuer Details"));
        } else {
            int nextPrice = nextPrice(target);
            buyPaper = named(Material.PAPER, "&6&lHomes Kaufen",
                    List.of("", "&7Gekauft: &b" + purchased + "&7/&b" + maxBuyable, "&7Naechster Slot: &6$" + nextPrice, "", "&7Klicke zum Kaufen"));
        }
        inv.setItem(53, buyPaper);

        UUID uuid = target.getUniqueId();
        guiSlotHome.put(uuid, new HashMap<>());
        guiSlotFree.put(uuid, new HashMap<>());
        guiSlotLocked.put(uuid, new HashMap<>());

        for (int i = 1; i <= maxTotal(); i++) {
            int slot = bedSlot(i);
            if (slot < 0) continue;

            guiSlotHome.get(uuid).put(slot, "");
            guiSlotFree.get(uuid).put(slot, 0);
            guiSlotLocked.get(uuid).put(slot, 0);

            if (i <= totalLimit) {
                if (i <= names.size()) {
                    String homeName = names.get(i-1);
                    ItemStack bed = named(Material.ORANGE_BED, "&b" + homeName,
                            List.of("", "&7Server: &b" + serverName(), "", "&aLinksklick: Teleport", "&cRechtsklick: Loeschen"));
                    inv.setItem(slot, bed);
                    guiSlotHome.get(uuid).put(slot, homeName);
                } else {
                    ItemStack bed = named(Material.WHITE_BED, "&fHome Slot " + i,
                            List.of("", "&7Dieser Slot ist frei.", "&7Nutze &e/sethome home" + i, "&7oder waehle einen eigenen Namen."));
                    inv.setItem(slot, bed);
                    guiSlotFree.get(uuid).put(slot, i);
                }
            } else {
                ItemStack bed = named(Material.GREEN_BED, "&aHome Slot " + i,
                        List.of("", "&7Dieser Slot ist noch nicht freigeschaltet.", "&7Klicke hier oder unten rechts,", "&7um weitere Homes zu kaufen."));
                inv.setItem(slot, bed);
                guiSlotLocked.get(uuid).put(slot, i);
            }
        }

        target.openInventory(inv);
    }

    public void openDeleteMenu(Player target, String homeName) {
        if (!exists(target, homeName)) {
            plugin.messages().error(target, "Home &e" + homeName + "&c does not exist.");
            openMainMenu(target);
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 5*9, deleteTitle());
        int[] border = new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,41,42,43,44};
        for (int s : border) inv.setItem(s, orangePane());

        inv.setItem(40, named(Material.BARRIER, "&cZurueck", null));
        inv.setItem(20, named(Material.RED_CONCRETE, "&cAbbrechen", null));

        inv.setItem(22, named(Material.PAPER, "&b" + homeName, List.of("")));

        inv.setItem(24, named(Material.LIME_CONCRETE, "&aBestaetigen",
                List.of("", "&cAchtung: Dieser Home wird", "&cendgueltig geloescht.")));

        deleteTarget.put(target.getUniqueId(), homeName);
        target.openInventory(inv);
    }

    public void openBuyMenu(Player target) {
        int rankHomes = rankLimit(target);
        int purchased = purchased(target);
        int maxBuyable = maxBuyable(target);
        int nextPrice = nextPrice(target);
        double balance = plugin.money().balance(target);

        Inventory inv = Bukkit.createInventory(null, 6*9, buyTitle());
        int[] border = new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,40,41,42,43,44,45,46,47,48,50,51,52,53};
        for (int s : border) inv.setItem(s, orangePane());

        inv.setItem(49, named(Material.BARRIER, "&cZurueck", null));

        inv.setItem(22, named(Material.CRAFTING_TABLE, "&b&lInfo",
                List.of("", "&7Rang Homes: &b" + rankHomes,
                        "&7Gekaufte Homes: &b" + purchased + "&7/&b" + maxBuyable,
                        "&7Naechster Preis: &6$" + nextPrice,
                        "&7Kontostand: &e$" + (int) balance)));

        ItemStack buyItem;
        if (purchased >= maxBuyable) {
            buyItem = named(Material.PAPER, "&6Home Kaufen", List.of("", "&cDu hast dein Kauf-Limit erreicht."));
        } else if (balance < nextPrice) {
            int missing = (int) Math.ceil(nextPrice - balance);
            buyItem = named(Material.PAPER, "&6Home Kaufen",
                    List.of("", "&7Preis: &6$" + nextPrice, "&cDir fehlen: $" + missing, "", "&7Verdiene mehr Geld und versuche es erneut."));
        } else {
            buyItem = named(Material.PAPER, "&6Home Kaufen", List.of("", "&7Preis: &6$" + nextPrice, "&aKlicke zum Kaufen"));
        }
        inv.setItem(25, buyItem);

        target.openInventory(inv);
    }

    public String getGuiHomeAt(Player player, int slot) {
        return guiSlotHome.getOrDefault(player.getUniqueId(), Map.of()).getOrDefault(slot, null);
    }

    public Integer getGuiFreeAt(Player player, int slot) {
        return guiSlotFree.getOrDefault(player.getUniqueId(), Map.of()).getOrDefault(slot, null);
    }

    public Integer getGuiLockedAt(Player player, int slot) {
        return guiSlotLocked.getOrDefault(player.getUniqueId(), Map.of()).getOrDefault(slot, null);
    }

    public String getDeleteTarget(Player player) {
        return deleteTarget.get(player.getUniqueId());
    }

    public void clearGuiState(Player player) {
        UUID u = player.getUniqueId();
        guiSlotHome.remove(u);
        guiSlotFree.remove(u);
        guiSlotLocked.remove(u);
        deleteTarget.remove(u);
    }

    public void refreshNameCache(Player player) {
        // Kein Cache aktiv: Daten werden direkt aus MySQL gelesen.
    }
}
