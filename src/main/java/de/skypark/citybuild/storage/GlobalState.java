package de.skypark.citybuild.storage;

import de.skypark.citybuild.core.CityBuildSettings;
import java.sql.*;

public class GlobalState {

  private final DataManager data;
  private final CityBuildSettings settings;

  public GlobalState(DataManager data, CityBuildSettings settings) {
    this.data = data;
    this.settings = settings;
    ensureDefaults();
  }

  private void ensureDefaults() {
    setIfMissing("debug.enabled", "false");
    setIfMissing("spawn.cooldown-seconds", String.valueOf(settings.spawnCooldownSeconds()));
  }

  private void setIfMissing(String key, String value) {
    if (get(key) != null) return;
    set(key, value);
  }

  public String get(String key) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps = c.prepareStatement("SELECT v FROM cb_globals WHERE k = ?")) {
      ps.setString(1, key);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;
        return rs.getString(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void set(String key, String value) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "INSERT INTO cb_globals (k, v) VALUES (?, ?) ON DUPLICATE KEY UPDATE v = VALUES(v)")) {
      ps.setString(1, key);
      ps.setString(2, value);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public boolean debugEnabled() {
    String v = get("debug.enabled");
    return v != null && v.equalsIgnoreCase("true");
  }

  public void setDebugEnabled(boolean enabled) {
    set("debug.enabled", enabled ? "true" : "false");
  }

  public int spawnCooldownSeconds() {
    String v = get("spawn.cooldown-seconds");
    if (v == null) return settings.spawnCooldownSeconds();
    try {
      return Math.max(0, Integer.parseInt(v));
    } catch (NumberFormatException ex) {
      return settings.spawnCooldownSeconds();
    }
  }

  public void setSpawnCooldownSeconds(int seconds) {
    set("spawn.cooldown-seconds", String.valueOf(Math.max(0, seconds)));
  }

  public String spawnLocationText() {
    String v = get("spawn.location");
    return v == null ? "" : v;
  }

  public void setSpawnLocationText(String text) {
    set("spawn.location", text == null ? "" : text);
  }
}
