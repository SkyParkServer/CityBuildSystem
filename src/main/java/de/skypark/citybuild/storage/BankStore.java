package de.skypark.citybuild.storage;

import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankStore {

    private final DataManager data;

    public BankStore(DataManager data) {
        this.data = data;
    }

    public double balance(OfflinePlayer player) {
        ensureRow(player);
        try (Connection c = data.db().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT balance FROM cb_bank WHERE uuid = ?")) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0D;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0D;
        }
    }

    public void setBalance(OfflinePlayer player, double amount) {
        ensureRow(player);
        try (Connection c = data.db().getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE cb_bank SET balance = ? WHERE uuid = ?")) {
            ps.setDouble(1, Math.max(0D, amount));
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deposit(OfflinePlayer player, double amount) {
        if (amount <= 0) {
            return;
        }
        setBalance(player, balance(player) + amount);
    }

    public boolean withdraw(OfflinePlayer player, double amount) {
        if (amount <= 0) {
            return true;
        }
        double current = balance(player);
        if (current < amount) {
            return false;
        }
        setBalance(player, current - amount);
        return true;
    }

    private void ensureRow(OfflinePlayer target) {
        try (Connection c = data.db().getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO cb_bank (uuid, balance) VALUES (?, 0)")) {
            ps.setString(1, target.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
