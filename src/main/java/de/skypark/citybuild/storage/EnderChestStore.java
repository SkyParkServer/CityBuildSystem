package de.skypark.citybuild.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class EnderChestStore {

  private final DataManager data;

  public EnderChestStore(DataManager data) {
    this.data = data;
  }

  public Inventory load(UUID uuid) {
    Inventory inventory = Bukkit.createInventory(null, 27, "Enderchest");
    String raw = getRaw(uuid);
    if (raw == null || raw.isEmpty()) {
      return inventory;
    }

    try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(raw));
        BukkitObjectInputStream ois = new BukkitObjectInputStream(bais)) {
      int size = ois.readInt();
      for (int i = 0; i < size; i++) {
        Object obj = ois.readObject();
        if (obj instanceof ItemStack item) {
          inventory.setItem(i, item);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return inventory;
  }

  public void save(UUID uuid, Inventory inventory) {
    String raw = serialize(inventory);
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement(
                "INSERT INTO cb_enderchest (uuid, contents) VALUES (?, ?) ON DUPLICATE KEY UPDATE contents = VALUES(contents)")) {
      ps.setString(1, uuid.toString());
      ps.setString(2, raw);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private String getRaw(UUID uuid) {
    try (Connection c = data.db().getConnection();
        PreparedStatement ps =
            c.prepareStatement("SELECT contents FROM cb_enderchest WHERE uuid = ?")) {
      ps.setString(1, uuid.toString());
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

  private String serialize(Inventory inventory) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BukkitObjectOutputStream oos = new BukkitObjectOutputStream(baos)) {
      oos.writeInt(inventory.getSize());
      for (int i = 0; i < inventory.getSize(); i++) {
        oos.writeObject(inventory.getItem(i));
      }
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }
}
