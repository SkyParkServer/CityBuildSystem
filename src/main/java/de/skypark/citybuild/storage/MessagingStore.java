package de.skypark.citybuild.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessagingStore {

  private final DataManager data;

  public MessagingStore(DataManager data) {
    this.data = data;
  }

  public boolean isEnabled(UUID uuid) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement("SELECT enabled FROM cb_msg_toggle WHERE uuid = ?")) {
      ps.setString(1, uuid.toString());
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return true;
        }
        return rs.getBoolean(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return true;
    }
  }

  public void setEnabled(UUID uuid, boolean enabled) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "INSERT INTO cb_msg_toggle (uuid, enabled) VALUES (?, ?) ON DUPLICATE KEY UPDATE enabled = VALUES(enabled)")) {
      ps.setString(1, uuid.toString());
      ps.setBoolean(2, enabled);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void updateLast(UUID from, UUID to) {
    long now = System.currentTimeMillis();
    try (Connection c = data.db().getConnection()) {
      try (PreparedStatement ps =
          c.prepareStatement(
              "INSERT INTO cb_msg_state (uuid, last_out_to, updated_at) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE last_out_to = VALUES(last_out_to), updated_at = VALUES(updated_at)")) {
        ps.setString(1, from.toString());
        ps.setString(2, to.toString());
        ps.setLong(3, now);
        ps.executeUpdate();
      }
      try (PreparedStatement ps =
          c.prepareStatement(
              "INSERT INTO cb_msg_state (uuid, last_in_from, updated_at) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE last_in_from = VALUES(last_in_from), updated_at = VALUES(updated_at)")) {
        ps.setString(1, to.toString());
        ps.setString(2, from.toString());
        ps.setLong(3, now);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public UUID lastReplyTarget(UUID sender) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "SELECT last_in_from, last_out_to FROM cb_msg_state WHERE uuid = ?")) {
      ps.setString(1, sender.toString());
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        String in = rs.getString(1);
        String out = rs.getString(2);
        String selected = in != null ? in : out;
        return selected == null || selected.isEmpty() ? null : UUID.fromString(selected);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public void queueMessage(UUID to, UUID from, String fromName, String message) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "INSERT INTO cb_messages (to_uuid, from_uuid, from_name, message, created_at, delivered) VALUES (?, ?, ?, ?, ?, 0)")) {
      ps.setString(1, to.toString());
      ps.setString(2, from.toString());
      ps.setString(3, fromName);
      ps.setString(4, message);
      ps.setLong(5, System.currentTimeMillis());
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<PendingMessage> pollUndelivered(UUID to) {
    List<PendingMessage> result = new ArrayList<>();
    try (Connection c = data.db().getConnection()) {
      try (PreparedStatement ps =
          c.prepareStatement(
              "SELECT id, from_uuid, from_name, message FROM cb_messages WHERE to_uuid = ? AND delivered = 0 ORDER BY id ASC")) {
        ps.setString(1, to.toString());
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            result.add(
                new PendingMessage(
                    rs.getLong(1),
                    UUID.fromString(rs.getString(2)),
                    rs.getString(3),
                    rs.getString(4)));
          }
        }
      }

      for (PendingMessage message : result) {
        try (PreparedStatement ps =
            c.prepareStatement("UPDATE cb_messages SET delivered = 1 WHERE id = ?")) {
          ps.setLong(1, message.id());
          ps.executeUpdate();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  public record PendingMessage(long id, UUID fromUuid, String fromName, String message) {}
}
