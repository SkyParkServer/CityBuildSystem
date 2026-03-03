package de.skypark.citybuild.core;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Mirrors 12_core_homes_sql.sk config defaults, but without SQL. Stored in homes-config.yml in the
 * plugin data folder.
 */
public class HomeConfig {

  private final JavaPlugin plugin;
  private final File file;
  private final FileConfiguration cfg;

  public HomeConfig(JavaPlugin plugin) {
    this.plugin = plugin;
    this.file = new File(plugin.getDataFolder(), "homes.yml");
    if (!file.exists()) {
      try {
        plugin.getDataFolder().mkdirs();
        file.createNewFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    this.cfg = YamlConfiguration.loadConfiguration(file);
    ensureDefaults();
  }

  private void ensureDefaults() {
    cfg.addDefault("server-name", plugin.getConfig().getString("server-name", "citybuild-1"));
    cfg.addDefault("max-total-homes", 15);
    cfg.addDefault("buy.base-price", 15000);
    cfg.addDefault("buy.step-price", 15000);
    cfg.addDefault("homes.default", 4);
    cfg.options().copyDefaults(true);
    save();
  }

  public void save() {
    try {
      cfg.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String serverName() {
    return cfg.getString("server-name", "citybuild-1");
  }

  public int maxTotalHomes() {
    int v = cfg.getInt("max-total-homes", 15);
    return Math.max(1, v);
  }

  public int basePrice() {
    return cfg.getInt("buy.base-price", 15000);
  }

  public int stepPrice() {
    return cfg.getInt("buy.step-price", 15000);
  }

  public int defaultRankHomes() {
    return cfg.getInt("homes.default", 4);
  }

  public Set<String> rankKeys() {
    if (!cfg.isConfigurationSection("homes")) return Set.of();
    return cfg.getConfigurationSection("homes").getKeys(false);
  }

  public int homesForRankKey(String key) {
    return cfg.getInt("homes." + key, 0);
  }
}
