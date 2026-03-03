package de.skypark.citybuild.core;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SpawnManager {

    private final CityBuildSystem plugin;
    private final File file;
    private final FileConfiguration cfg;

    public SpawnManager(CityBuildSystem plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "spawn.yml");
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Konnte spawn.yml nicht erstellen.", e);
            }
        }
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public Location getSpawn() {
        return LocationUtil.textToLoc(cfg.getString("spawn.location", ""));
    }

    public void setSpawn(Location location) {
        cfg.set("spawn.location", LocationUtil.locToText(location));
        save();
    }

    public boolean teleportOnJoinEnabled() {
        return cfg.getBoolean("spawn.teleport-on-join", false);
    }

    public void setTeleportOnJoinEnabled(boolean enabled) {
        cfg.set("spawn.teleport-on-join", enabled);
        save();
    }

    private void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte spawn.yml nicht speichern: " + e.getMessage());
        }
    }
}
