package de.skypark.citybuild.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class TresorStore {

  private final DataManager data;

  public TresorStore(DataManager data) {
    this.data = data;
  }

  public ItemStack[] loadPage(UUID uuid, int page, int expectedSize) {
    ItemStack[] items = new ItemStack[expectedSize];
    String raw = getRaw(uuid, page);
    if (raw == null || raw.isEmpty()) {
      return items;
    }

    try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(raw));
        BukkitObjectInputStream ois = new BukkitObjectInputStream(bais)) {
      int size = ois.readInt();
      for (int i = 0; i < size && i < expectedSize; i++) {
        Object obj = ois.readObject();
        if (obj instanceof ItemStack item) {
          items[i] = item;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return items;
  }

  public void savePage(UUID uuid, int page, ItemStack[] items) {
    String raw = serialize(items);
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "INSERT INTO cb_tresor (uuid, page, contents) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE contents = VALUES(contents)")) {
      ps.setString(1, uuid.toString());
      ps.setInt(2, page);
      ps.setString(3, raw);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private String getRaw(UUID uuid, int page) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement("SELECT contents FROM cb_tresor WHERE uuid = ? AND page = ?")) {
      ps.setString(1, uuid.toString());
      ps.setInt(2, page);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getString(1);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String serialize(ItemStack[] items) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BukkitObjectOutputStream oos = new BukkitObjectOutputStream(baos)) {
      oos.writeInt(items.length);
      for (ItemStack item : items) {
        oos.writeObject(item);
      }
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }
}
