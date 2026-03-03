package de.skypark.citybuild.storage;

import java.sql.*;
import org.bukkit.OfflinePlayer;

public class MoneyStore {

  private final DataManager data;

  public MoneyStore(DataManager data) {
    this.data = data;
  }

  private void ensureRow(String uuid, String name) throws SQLException {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement("INSERT IGNORE INTO cb_players (uuid, last_name) VALUES (?, ?)")) {
      ps.setString(1, uuid);
      ps.setString(2, name);
      ps.executeUpdate();
    }
  }

  public double balance(OfflinePlayer target) {
    String uuid = target.getUniqueId().toString();
    try {
      ensureRow(uuid, target.getName());
      try (Connection c = data.db().getConnection();
          PreparedStatement ps =
              c.prepareStatement("SELECT money FROM cb_players WHERE uuid = ?")) {
        ps.setString(1, uuid);
        try (ResultSet rs = ps.executeQuery()) {
          return rs.next() ? rs.getDouble(1) : 0D;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return 0D;
    }
  }

  public void setMoney(OfflinePlayer target, double amount) {
    if (amount < 0) amount = 0;
    String uuid = target.getUniqueId().toString();
    try {
      ensureRow(uuid, target.getName());
      try (Connection c = data.db().getConnection();
          PreparedStatement ps =
              c.prepareStatement("UPDATE cb_players SET money = ? WHERE uuid = ?")) {
        ps.setDouble(1, amount);
        ps.setString(2, uuid);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void addMoney(OfflinePlayer target, double amount) {
    if (amount <= 0) return;
    String uuid = target.getUniqueId().toString();
    try {
      ensureRow(uuid, target.getName());
      try (Connection c = data.db().getConnection();
          PreparedStatement ps =
              c.prepareStatement("UPDATE cb_players SET money = money + ? WHERE uuid = ?")) {
        ps.setDouble(1, amount);
        ps.setString(2, uuid);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public boolean takeMoney(OfflinePlayer target, double amount) {
    if (amount <= 0) return true;
    String uuid = target.getUniqueId().toString();
    try {
      ensureRow(uuid, target.getName());
      try (Connection c = data.db().getConnection()) {
        c.setAutoCommit(false);

        double cur = 0D;
        try (PreparedStatement sel =
            c.prepareStatement("SELECT money FROM cb_players WHERE uuid = ? FOR UPDATE")) {
          sel.setString(1, uuid);
          try (ResultSet rs = sel.executeQuery()) {
            if (rs.next()) cur = rs.getDouble(1);
          }
        }

        if (cur < amount) {
          c.rollback();
          return false;
        }

        try (PreparedStatement upd =
            c.prepareStatement("UPDATE cb_players SET money = money - ? WHERE uuid = ?")) {
          upd.setDouble(1, amount);
          upd.setString(2, uuid);
          upd.executeUpdate();
        }

        c.commit();
        return true;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}
