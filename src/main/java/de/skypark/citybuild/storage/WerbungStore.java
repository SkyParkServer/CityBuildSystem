package de.skypark.citybuild.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WerbungStore {

  private final DataManager data;

  public WerbungStore(DataManager data) {
    this.data = data;
  }

  public long getLastUsed(UUID uuid) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement("SELECT last_used FROM cb_werbung_cooldown WHERE uuid = ?")) {
      ps.setString(1, uuid.toString());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getLong(1);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0L;
  }

  public void setLastUsed(UUID uuid, String serverName, Location location) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "INSERT INTO cb_werbung_cooldown (uuid, last_used, server_name, world_name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE last_used = VALUES(last_used), server_name = VALUES(server_name), world_name = VALUES(world_name), x = VALUES(x), y = VALUES(y), z = VALUES(z), yaw = VALUES(yaw), pitch = VALUES(pitch)")) {
      ps.setString(1, uuid.toString());
      ps.setLong(2, System.currentTimeMillis());
      ps.setString(3, serverName);
      ps.setString(4, location.getWorld().getName());
      ps.setDouble(5, location.getX());
      ps.setDouble(6, location.getY());
      ps.setDouble(7, location.getZ());
      ps.setFloat(8, location.getYaw());
      ps.setFloat(9, location.getPitch());
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Location getTeleportTarget(UUID uuid) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "SELECT server_name, world_name, x, y, z, yaw, pitch FROM cb_werbung_cooldown WHERE uuid = ?")) {
      ps.setString(1, uuid.toString());
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        World world = Bukkit.getWorld(rs.getString(2));
        if (world == null) {
          return null;
        }
        return new Location(
            world,
            rs.getDouble(3),
            rs.getDouble(4),
            rs.getDouble(5),
            rs.getFloat(6),
            rs.getFloat(7));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
