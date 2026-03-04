package de.skypark.citybuild.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class FarmConfig {

  private final JavaPlugin plugin;
  private final File file;
  private FileConfiguration cfg;

  public FarmConfig(JavaPlugin plugin) {
    this.plugin = plugin;
    this.file = new File(plugin.getDataFolder(), "farm.yml");

    if (!file.exists()) {
      plugin.saveResource("farm.yml", false);
    }

    this.cfg = YamlConfiguration.loadConfiguration(file);
    ensureDefaults();
  }

  private void ensureDefaults() {
    cfg.addDefault("menu.title", "&6&lFarmwelten");
    cfg.addDefault("menu.ping-timeout-ms", 1200);
    cfg.addDefault("menu.close-item.material", "BARRIER");
    cfg.addDefault("menu.close-item.name", "&cSchliessen");
    cfg.addDefault("menu.close-item.lore", List.of("&7Klicke, um das Menue zu schliessen."));
    cfg.addDefault("menu.status.online", "&aOnline");
    cfg.addDefault("menu.status.maintenance", "&6Wartungsarbeiten");
    cfg.options().copyDefaults(true);
    save();
  }

  public void save() {
    try {
      cfg.save(file);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String menuTitle() {
    return cfg.getString("menu.title", "&6&lFarmwelten");
  }

  public int pingTimeoutMs() {
    int timeout = cfg.getInt("menu.ping-timeout-ms", 1200);
    return Math.max(250, timeout);
  }

  public Material closeMaterial() {
    return parseMaterial(cfg.getString("menu.close-item.material", "BARRIER"), Material.BARRIER);
  }

  public String closeName() {
    return cfg.getString("menu.close-item.name", "&cSchliessen");
  }

  public List<String> closeLore() {
    return cfg.getStringList("menu.close-item.lore");
  }

  public String statusOnlineText() {
    return cfg.getString("menu.status.online", "&aOnline");
  }

  public String statusMaintenanceText() {
    return cfg.getString("menu.status.maintenance", "&6Wartungsarbeiten");
  }

  public List<FarmServerDefinition> servers() {
    ConfigurationSection section = cfg.getConfigurationSection("servers");
    if (section == null) {
      return List.of();
    }

    List<FarmServerDefinition> entries = new ArrayList<>();
    for (String key : section.getKeys(false)) {
      String base = "servers." + key + ".";
      if (!cfg.getBoolean(base + "enabled", true)) {
        continue;
      }

      String proxyServer = cfg.getString(base + "server", key);
      String maintenanceServer = cfg.getString(base + "maintenance-server", proxyServer);
      String host = cfg.getString(base + "host", "127.0.0.1");
      int port = cfg.getInt(base + "port", 25565);
      int slot = cfg.getInt(base + "slot", 22);
      Material material =
          parseMaterial(cfg.getString(base + "material", "GRASS_BLOCK"), Material.GRASS_BLOCK);
      String displayName = cfg.getString(base + "name", "&6" + proxyServer);
      List<String> lore = cfg.getStringList(base + "lore");
      List<String> maintenanceMarkers = cfg.getStringList(base + "maintenance-markers");

      entries.add(
          new FarmServerDefinition(
              key,
              proxyServer,
              maintenanceServer,
              host,
              Math.max(1, Math.min(65535, port)),
              slot,
              material,
              displayName,
              lore,
              maintenanceMarkers));
    }

    entries.sort(Comparator.comparingInt(FarmServerDefinition::slot));
    return entries;
  }

  private Material parseMaterial(String value, Material fallback) {
    if (value == null || value.isBlank()) {
      return fallback;
    }

    Material material = Material.matchMaterial(value.toUpperCase(Locale.ROOT));
    return material == null ? fallback : material;
  }

  public record FarmServerDefinition(
      String key,
      String server,
      String maintenanceServer,
      String host,
      int port,
      int slot,
      Material material,
      String displayName,
      List<String> lore,
      List<String> maintenanceMarkers) {}
}
