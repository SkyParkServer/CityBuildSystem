package de.skypark.citybuild.core;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/** Mirrors 00_options.sk defaults & constants. */
public class CityBuildSettings {

  private final JavaPlugin plugin;

  public CityBuildSettings(JavaPlugin plugin) {
    this.plugin = plugin;
    ensureDefaults();
  }

  private void ensureDefaults() {
    FileConfiguration c = plugin.getConfig();

    // Skript on load constants:
    c.addDefault("const.prefix", "&6&lSkyPark &8»");
    c.addDefault("const.no-perm", "&7Du hast dazu keine Rechte!");
    c.addDefault("const.default-spawn-cooldown-seconds", 5);
    c.addDefault("const.default-home-limit", 1);

    // Persistent state defaults (were Skript variables)
    c.addDefault("spawn.cooldown-seconds", c.getInt("const.default-spawn-cooldown-seconds", 5));
    c.addDefault("debug.enabled", false);

    c.options().copyDefaults(true);
    plugin.saveConfig();
  }

  public String prefix() {
    return plugin.getConfig().getString("const.prefix", "&6&lSkyPark &8»");
  }

  public String noPermissionMessage() {
    return plugin.getConfig().getString("const.no-perm", "&7Du hast dazu keine Rechte!");
  }

  public int spawnCooldownSeconds() {
    return plugin
        .getConfig()
        .getInt(
            "spawn.cooldown-seconds",
            plugin.getConfig().getInt("const.default-spawn-cooldown-seconds", 5));
  }

  public int defaultHomeLimit() {
    return plugin.getConfig().getInt("const.default-home-limit", 1);
  }

  public boolean debugEnabled() {
    return plugin.getConfig().getBoolean("debug.enabled", false);
  }
}
