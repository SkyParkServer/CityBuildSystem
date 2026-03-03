package de.skypark.citybuild.storage;

import de.skypark.citybuild.CityBuildSystem;
import java.sql.*;

public class DatabaseManager {

  private final CityBuildSystem plugin;
  private final String jdbcUrl;
  private final String user;
  private final String password;

  public DatabaseManager(CityBuildSystem plugin) {
    this.plugin = plugin;

    String host = plugin.getConfig().getString("mysql.host", "127.0.0.1");
    int port = plugin.getConfig().getInt("mysql.port", 3306);
    String db = plugin.getConfig().getString("mysql.database", "citybuild");
    this.user = plugin.getConfig().getString("mysql.user", "root");
    this.password = plugin.getConfig().getString("mysql.password", "");
    boolean useSSL = plugin.getConfig().getBoolean("mysql.useSSL", false);

    this.jdbcUrl =
        "jdbc:mysql://"
            + host
            + ":"
            + port
            + "/"
            + db
            + "?useSSL="
            + useSSL
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC"
            + "&characterEncoding=utf8";
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(jdbcUrl, user, password);
  }

  public void initSchema() {
    try (Connection c = getConnection();
        Statement st = c.createStatement()) {
      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_globals (
                  k VARCHAR(64) PRIMARY KEY,
                  v TEXT NOT NULL
                ) CHARACTER SET utf8mb4
            """);
      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_players (
                  uuid VARCHAR(36) PRIMARY KEY,
                  first_join BIGINT NULL,
                  last_join BIGINT NULL,
                  last_seen BIGINT NULL,
                  last_name VARCHAR(32) NULL,
                  home_limit INT NOT NULL DEFAULT 1,
                  money DOUBLE NOT NULL DEFAULT 0,
                  purchased_homes INT NOT NULL DEFAULT 0
                ) CHARACTER SET utf8mb4
            """);
      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_cooldowns (
                  uuid VARCHAR(36) NOT NULL,
                  name VARCHAR(32) NOT NULL,
                  expires BIGINT NOT NULL,
                  PRIMARY KEY (uuid, name)
                ) CHARACTER SET utf8mb4
            """);
      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_enderchest (
                  uuid VARCHAR(36) PRIMARY KEY,
                  contents MEDIUMTEXT NOT NULL
                ) CHARACTER SET utf8mb4
            """);
      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_tresor (
                  uuid VARCHAR(36) NOT NULL,
                  page INT NOT NULL,
                  contents MEDIUMTEXT NOT NULL,
                  PRIMARY KEY (uuid, page)
                ) CHARACTER SET utf8mb4
            """);
      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_warps (
                  server_name VARCHAR(64) NOT NULL,
                  warp_name VARCHAR(32) NOT NULL,
                  world_name VARCHAR(64) NOT NULL,
                  x DOUBLE NOT NULL,
                  y DOUBLE NOT NULL,
                  z DOUBLE NOT NULL,
                  yaw FLOAT NOT NULL,
                  pitch FLOAT NOT NULL,
                  updated_at BIGINT NOT NULL,
                  PRIMARY KEY (server_name, warp_name)
                ) CHARACTER SET utf8mb4
            """);

      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_msg_toggle (
                  uuid VARCHAR(36) PRIMARY KEY,
                  enabled TINYINT(1) NOT NULL DEFAULT 1
                ) CHARACTER SET utf8mb4
            """);
      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_msg_state (
                  uuid VARCHAR(36) PRIMARY KEY,
                  last_in_from VARCHAR(36) NULL,
                  last_out_to VARCHAR(36) NULL,
                  updated_at BIGINT NOT NULL
                ) CHARACTER SET utf8mb4
            """);
      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_messages (
                  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                  to_uuid VARCHAR(36) NOT NULL,
                  from_uuid VARCHAR(36) NOT NULL,
                  from_name VARCHAR(32) NOT NULL,
                  message TEXT NOT NULL,
                  created_at BIGINT NOT NULL,
                  delivered TINYINT(1) NOT NULL DEFAULT 0
                ) CHARACTER SET utf8mb4
            """);

      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_crystals (
                  uuid VARCHAR(36) PRIMARY KEY,
                  amount INT NOT NULL DEFAULT 0
                ) CHARACTER SET utf8mb4
            """);

      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_bank (
                  uuid VARCHAR(36) PRIMARY KEY,
                  balance DOUBLE NOT NULL DEFAULT 0
                ) CHARACTER SET utf8mb4
            """);
      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_werbung_cooldown (
                  uuid VARCHAR(36) PRIMARY KEY,
                  last_used BIGINT NOT NULL,
                  server_name VARCHAR(64) NOT NULL,
                  world_name VARCHAR(64) NOT NULL,
                  x DOUBLE NOT NULL,
                  y DOUBLE NOT NULL,
                  z DOUBLE NOT NULL,
                  yaw FLOAT NOT NULL,
                  pitch FLOAT NOT NULL
                ) CHARACTER SET utf8mb4
            """);

      st.executeUpdate(
          """
                CREATE TABLE IF NOT EXISTS cb_homes (
                  uuid VARCHAR(36) NOT NULL,
                  server_name VARCHAR(64) NOT NULL,
                  home_name VARCHAR(32) NOT NULL,
                  world_name VARCHAR(64) NOT NULL,
                  x DOUBLE NOT NULL,
                  y DOUBLE NOT NULL,
                  z DOUBLE NOT NULL,
                  yaw FLOAT NOT NULL,
                  pitch FLOAT NOT NULL,
                  updated_at BIGINT NOT NULL,
                  PRIMARY KEY (uuid, server_name, home_name)
                ) CHARACTER SET utf8mb4
            """);
    } catch (SQLException e) {
      plugin.getLogger().severe("Failed to initialize MySQL schema: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
