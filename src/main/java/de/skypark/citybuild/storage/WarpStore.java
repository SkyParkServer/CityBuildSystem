package de.skypark.citybuild.storage;

import de.skypark.citybuild.util.LocationUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

public class WarpStore {

  private final DataManager data;

  public WarpStore(DataManager data) {
    this.data = data;
  }

  public void setWarp(String serverName, String warpName, Location location) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "INSERT INTO cb_warps (server_name, warp_name, world_name, x, y, z, yaw, pitch, updated_at) VALUES (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE world_name=VALUES(world_name), x=VALUES(x), y=VALUES(y), z=VALUES(z), yaw=VALUES(yaw), pitch=VALUES(pitch), updated_at=VALUES(updated_at)")) {
      ps.setString(1, serverName);
      ps.setString(2, warpName.toLowerCase());
      ps.setString(3, location.getWorld().getName());
      ps.setDouble(4, location.getX());
      ps.setDouble(5, location.getY());
      ps.setDouble(6, location.getZ());
      ps.setFloat(7, location.getYaw());
      ps.setFloat(8, location.getPitch());
      ps.setLong(9, System.currentTimeMillis());
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public boolean deleteWarp(String serverName, String warpName) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement("DELETE FROM cb_warps WHERE server_name = ? AND warp_name = ?")) {
      ps.setString(1, serverName);
      ps.setString(2, warpName.toLowerCase());
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public Location getWarp(String serverName, String warpName) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "SELECT world_name, x, y, z, yaw, pitch FROM cb_warps WHERE server_name = ? AND warp_name = ?")) {
      ps.setString(1, serverName);
      ps.setString(2, warpName.toLowerCase());
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        return LocationUtil.textToLoc(
            rs.getString(1)
                + "|"
                + rs.getDouble(2)
                + "|"
                + rs.getDouble(3)
                + "|"
                + rs.getDouble(4)
                + "|"
                + rs.getFloat(5)
                + "|"
                + rs.getFloat(6));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public List<String> listWarps(String serverName) {
    List<String> names = new ArrayList<>();
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "SELECT warp_name FROM cb_warps WHERE server_name = ? ORDER BY warp_name ASC")) {
      ps.setString(1, serverName);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          names.add(rs.getString(1));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return names;
  }
}
