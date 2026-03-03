package de.skypark.citybuild.storage;

import java.sql.*;
import java.time.Duration;
import org.bukkit.entity.Player;

public class CooldownStore {

  private final DataManager data;

  public CooldownStore(DataManager data) {
    this.data = data;
  }

  public Duration cooldownRemaining(String cooldownName, Player target) {
    String uuid = target.getUniqueId().toString();
    long now = System.currentTimeMillis();

    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement("SELECT expires FROM cb_cooldowns WHERE uuid = ? AND name = ?")) {
      ps.setString(1, uuid);
      ps.setString(2, cooldownName);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return Duration.ZERO;
        long expires = rs.getLong(1);
        if (expires <= now) return Duration.ZERO;
        return Duration.ofMillis(expires - now);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return Duration.ZERO;
    }
  }

  public boolean hasCooldown(String cooldownName, Player target) {
    return cooldownRemaining(cooldownName, target).compareTo(Duration.ZERO) > 0;
  }

  public void startCooldown(String cooldownName, Player target, double durationSeconds) {
    long now = System.currentTimeMillis();
    long durMillis = (long) Math.max(0, durationSeconds * 1000.0);
    long expires = now + durMillis;

    String uuid = target.getUniqueId().toString();

    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "INSERT INTO cb_cooldowns (uuid, name, expires) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE expires = VALUES(expires)")) {
      ps.setString(1, uuid);
      ps.setString(2, cooldownName);
      ps.setLong(3, expires);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
