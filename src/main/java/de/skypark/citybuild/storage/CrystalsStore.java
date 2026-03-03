package de.skypark.citybuild.storage;

import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrystalsStore {

    private final DataManager data;

    public CrystalsStore(DataManager data) {
        this.data = data;
    }

    public int get(OfflinePlayer target) {
        ensureRow(target);
        try (Connection c = data.db().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT amount FROM cb_crystals WHERE uuid = ?")) {
            ps.setString(1, target.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void set(OfflinePlayer target, int amount) {
        ensureRow(target);
        try (Connection c = data.db().getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE cb_crystals SET amount = ? WHERE uuid = ?")) {
            ps.setInt(1, Math.max(0, amount));
            ps.setString(2, target.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add(OfflinePlayer target, int amount) {
        if (amount <= 0) {
            return;
        }
        set(target, get(target) + amount);
    }

    public void reset(OfflinePlayer target) {
        set(target, 0);
    }

    private void ensureRow(OfflinePlayer target) {
        try (Connection c = data.db().getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO cb_crystals (uuid, amount) VALUES (?, 0)")) {
            ps.setString(1, target.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
