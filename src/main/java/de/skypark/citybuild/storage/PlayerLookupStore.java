package de.skypark.citybuild.storage;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerLookupStore {

    private final DataManager data;

    public PlayerLookupStore(DataManager data) {
        this.data = data;
    }

    public UUID findUuidByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        org.bukkit.entity.Player online = Bukkit.getPlayerExact(name);
        if (online != null) {
            return online.getUniqueId();
        }

        try (Connection c = data.db().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT uuid FROM cb_players WHERE LOWER(last_name) = LOWER(?) ORDER BY last_join DESC LIMIT 1")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String findNameByUuid(UUID uuid) {
        try (Connection c = data.db().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT last_name FROM cb_players WHERE uuid = ? LIMIT 1")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        OfflinePlayer fallback = Bukkit.getOfflinePlayer(uuid);
        return fallback.getName() == null ? uuid.toString() : fallback.getName();
    }
}
