package de.skypark.citybuild.storage;

import de.skypark.citybuild.core.CityBuildSettings;
import java.sql.*;
import org.bukkit.entity.Player;

public class PlayerDataStore {

  private final DataManager data;
  private final CityBuildSettings settings;

  public PlayerDataStore(DataManager data, CityBuildSettings settings) {
    this.data = data;
    this.settings = settings;
  }

  public void ensureFirstJoin(Player player) {
    String uuid = player.getUniqueId().toString();
    long now = System.currentTimeMillis();

    try (Connection c = data.db().getConnection()) {
      boolean exists;
      try (PreparedStatement ps =
          c.prepareStatement("SELECT uuid FROM cb_players WHERE uuid = ?")) {
        ps.setString(1, uuid);
        try (ResultSet rs = ps.executeQuery()) {
          exists = rs.next();
        }
      }

      if (!exists) {
        try (PreparedStatement ins =
            c.prepareStatement(
                "INSERT INTO cb_players (uuid, first_join, last_name, home_limit, money, purchased_homes) VALUES (?, ?, ?, ?, 0, 0)")) {
          ins.setString(1, uuid);
          ins.setLong(2, now);
          ins.setString(3, player.getName());
          ins.setInt(4, settings.defaultHomeLimit());
          ins.executeUpdate();
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void setLastJoin(Player player) {
    ensureFirstJoin(player);
    String uuid = player.getUniqueId().toString();
    long now = System.currentTimeMillis();
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "UPDATE cb_players SET last_join = ?, last_name = ? WHERE uuid = ?")) {
      ps.setLong(1, now);
      ps.setString(2, player.getName());
      ps.setString(3, uuid);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void setLastSeen(Player player) {
    ensureFirstJoin(player);
    String uuid = player.getUniqueId().toString();
    long now = System.currentTimeMillis();
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "UPDATE cb_players SET last_seen = ?, last_name = ? WHERE uuid = ?")) {
      ps.setLong(1, now);
      ps.setString(2, player.getName());
      ps.setString(3, uuid);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public int getHomeLimit(Player player) {
    ensureFirstJoin(player);
    String uuid = player.getUniqueId().toString();
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement("SELECT home_limit FROM cb_players WHERE uuid = ?")) {
      ps.setString(1, uuid);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) : settings.defaultHomeLimit();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return settings.defaultHomeLimit();
    }
  }

  public void setHomeLimit(Player player, int limit) {
    ensureFirstJoin(player);
    limit = Math.max(0, limit);
    String uuid = player.getUniqueId().toString();
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement("UPDATE cb_players SET home_limit = ? WHERE uuid = ?")) {
      ps.setInt(1, limit);
      ps.setString(2, uuid);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
