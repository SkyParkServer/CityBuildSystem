package de.skypark.citybuild.core;

import de.skypark.citybuild.CityBuildSystem;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FarmMenuService {

  private static final Pattern ONLINE_PLAYERS_PATTERN =
      Pattern.compile("\\\"online\\\"\\s*:\\s*(\\d+)");
  private static final Pattern MOTD_TEXT_PATTERN =
      Pattern.compile("\\\"text\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");

  private final CityBuildSystem plugin;
  private final FarmConfig farmConfig;

  public FarmMenuService(CityBuildSystem plugin, FarmConfig farmConfig) {
    this.plugin = plugin;
    this.farmConfig = farmConfig;
  }

  public void openMenu(Player player) {
    List<FarmConfig.FarmServerDefinition> definitions = farmConfig.servers();
    int timeoutMs = farmConfig.pingTimeoutMs();

    Bukkit.getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              List<DisplayServer> onlineServers = new ArrayList<>();
              Set<String> maintenanceServers = fetchMaintenanceServers();

              for (FarmConfig.FarmServerDefinition definition : definitions) {
                PingResult pingResult = pingStatus(definition.host(), definition.port(), timeoutMs);
                if (pingResult == null) {
                  continue;
                }

                boolean maintenance =
                    resolveMaintenance(definition, maintenanceServers, pingResult);
                onlineServers.add(
                    new DisplayServer(
                        definition,
                        pingResult.onlinePlayers(),
                        maintenance,
                        sanitizeText(extractMotd(pingResult.rawResponse()))));
              }

              Bukkit.getScheduler()
                  .runTask(
                      plugin,
                      () -> {
                        if (!player.isOnline()) {
                          return;
                        }
                        openBuiltMenu(player, onlineServers);
                      });
            });
  }

  public void connect(Player player, String serverName) {
    try {
      ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(byteArray);
      out.writeUTF("Connect");
      out.writeUTF(serverName);

      player.sendPluginMessage(plugin, "BungeeCord", byteArray.toByteArray());
    } catch (IOException e) {
      plugin.messages().error(player, "Verbindung zum Zielserver fehlgeschlagen.");
      e.printStackTrace();
    }
  }

  private void openBuiltMenu(Player player, List<DisplayServer> onlineServers) {
    Map<Integer, String> serverBySlot = new HashMap<>();
    int closeSlot = 49;
    FarmMenuHolder holder = new FarmMenuHolder(serverBySlot, closeSlot);
    Inventory inventory =
        Bukkit.createInventory(holder, 54, plugin.messages().color(farmConfig.menuTitle()));
    holder.setInventory(inventory);

    ItemStack border = named(Material.ORANGE_STAINED_GLASS_PANE, "&6 ", List.of());
    for (int slot : borderSlots()) {
      inventory.setItem(slot, border);
    }

    inventory.setItem(
        closeSlot,
        named(farmConfig.closeMaterial(), farmConfig.closeName(), farmConfig.closeLore()));

    for (DisplayServer server : onlineServers) {
      int slot = server.definition().slot();
      if (slot < 0 || slot >= inventory.getSize() || slot == closeSlot) {
        continue;
      }

      String maintenanceStatus =
          server.maintenance() ? farmConfig.statusMaintenanceText() : farmConfig.statusOnlineText();

      Map<String, String> placeholders = new HashMap<>();
      placeholders.put("%server%", server.definition().server());
      placeholders.put("%server_key%", server.definition().key());
      placeholders.put("%online_players%", String.valueOf(server.onlinePlayers()));
      placeholders.put("%online%", String.valueOf(server.onlinePlayers()));
      placeholders.put("%motd%", server.motd());
      placeholders.put("%maintenance%", maintenanceStatus);
      placeholders.put("%maintenance_status%", maintenanceStatus);

      String name = applyPlaceholders(server.definition().displayName(), placeholders);
      List<String> lore = applyPlaceholders(server.definition().lore(), placeholders);

      inventory.setItem(slot, named(server.definition().material(), name, lore));
      serverBySlot.put(slot, server.definition().server());
    }

    if (serverBySlot.isEmpty()) {
      inventory.setItem(
          22,
          named(
              Material.BARRIER,
              "&cKeine Farmwelten verfuegbar",
              List.of("&7Aktuell ist kein Farm-Server online.")));
    }

    player.openInventory(inventory);
  }

  private boolean resolveMaintenance(
      FarmConfig.FarmServerDefinition definition,
      Set<String> maintenanceServers,
      PingResult pingResult) {
    for (String serverName : maintenanceServers) {
      if (serverName.equalsIgnoreCase(definition.maintenanceServer())) {
        return true;
      }
    }

    String loweredResponse = pingResult.rawResponse().toLowerCase(Locale.ROOT);
    for (String marker : definition.maintenanceMarkers()) {
      if (marker == null || marker.isBlank()) {
        continue;
      }
      if (loweredResponse.contains(marker.toLowerCase(Locale.ROOT))) {
        return true;
      }
    }

    return false;
  }

  private Set<String> fetchMaintenanceServers() {
    Set<String> names = new HashSet<>();
    try {
      Class<?> providerClass = Class.forName("eu.kennytv.maintenance.api.MaintenanceProvider");
      Object maintenance = providerClass.getMethod("get").invoke(null);
      if (maintenance == null) {
        return names;
      }

      try {
        Object values =
            maintenance.getClass().getMethod("getMaintenanceServers").invoke(maintenance);
        if (values instanceof Iterable<?> iterable) {
          for (Object value : iterable) {
            if (value != null) {
              names.add(value.toString());
            }
          }
        }
      } catch (NoSuchMethodException ignored) {
        if (Boolean.TRUE.equals(
            maintenance.getClass().getMethod("isMaintenance").invoke(maintenance))) {
          names.add(plugin.getConfig().getString("server-name", ""));
        }
      }
    } catch (Exception ignored) {
      return names;
    }
    return names;
  }

  private List<Integer> borderSlots() {
    List<Integer> border = new ArrayList<>();
    for (int col = 0; col < 9; col++) {
      border.add(col);
      border.add(45 + col);
    }
    for (int row = 1; row <= 4; row++) {
      border.add(row * 9);
      border.add((row * 9) + 8);
    }
    return border;
  }

  private PingResult pingStatus(String host, int port, int timeoutMs) {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), timeoutMs);
      socket.setSoTimeout(timeoutMs);

      DataOutputStream output = new DataOutputStream(socket.getOutputStream());
      InputStream input = socket.getInputStream();

      ByteArrayOutputStream handshakeBytes = new ByteArrayOutputStream();
      DataOutputStream handshake = new DataOutputStream(handshakeBytes);
      writeVarInt(handshake, 0x00);
      writeVarInt(handshake, 767);
      writeString(handshake, host);
      handshake.writeShort(port);
      writeVarInt(handshake, 1);

      writeVarInt(output, handshakeBytes.size());
      output.write(handshakeBytes.toByteArray());

      output.writeByte(0x01);
      output.writeByte(0x00);
      output.flush();

      readVarInt(input);
      int packetId = readVarInt(input);
      if (packetId != 0x00) {
        return null;
      }

      String json = readString(input);
      Matcher onlineMatcher = ONLINE_PLAYERS_PATTERN.matcher(json);
      int onlinePlayers = onlineMatcher.find() ? Integer.parseInt(onlineMatcher.group(1)) : 0;
      return new PingResult(onlinePlayers, json);
    } catch (Exception e) {
      return null;
    }
  }

  private String extractMotd(String response) {
    Matcher matcher = MOTD_TEXT_PATTERN.matcher(response);
    if (!matcher.find()) {
      return "";
    }
    return matcher.group(1);
  }

  private String sanitizeText(String input) {
    if (input == null || input.isBlank()) {
      return "";
    }
    return input.replace("\\n", " ").replace("\\\"", "\"").replace("\\u00a7", "").trim();
  }

  private ItemStack named(Material material, String name, List<String> lore) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(plugin.messages().color(name));
    if (lore != null && !lore.isEmpty()) {
      meta.setLore(lore.stream().filter(Objects::nonNull).map(plugin.messages()::color).toList());
    }
    item.setItemMeta(meta);
    return item;
  }

  private String applyPlaceholders(String text, Map<String, String> placeholders) {
    if (text == null) {
      return "";
    }
    String value = text;
    for (Map.Entry<String, String> entry : placeholders.entrySet()) {
      value = value.replace(entry.getKey(), entry.getValue());
    }
    return value;
  }

  private List<String> applyPlaceholders(List<String> lines, Map<String, String> placeholders) {
    List<String> replaced = new ArrayList<>();
    for (String line : lines) {
      replaced.add(applyPlaceholders(line, placeholders));
    }
    return replaced;
  }

  private static void writeString(DataOutputStream out, String value) throws IOException {
    byte[] data = value.getBytes(StandardCharsets.UTF_8);
    writeVarInt(out, data.length);
    out.write(data);
  }

  private static String readString(InputStream in) throws IOException {
    int length = readVarInt(in);
    byte[] data = in.readNBytes(length);
    if (data.length != length) {
      throw new IOException("Incomplete string packet");
    }
    return new String(data, StandardCharsets.UTF_8);
  }

  private static void writeVarInt(DataOutputStream out, int value) throws IOException {
    while ((value & 0xFFFFFF80) != 0) {
      out.writeByte((value & 0x7F) | 0x80);
      value >>>= 7;
    }
    out.writeByte(value & 0x7F);
  }

  private static int readVarInt(InputStream in) throws IOException {
    int numRead = 0;
    int result = 0;
    int read;
    do {
      read = in.read();
      if (read == -1) {
        throw new IOException("Unexpected end of stream");
      }

      int value = (read & 0x7F);
      result |= (value << (7 * numRead));
      numRead++;
      if (numRead > 5) {
        throw new IOException("VarInt is too big");
      }
    } while ((read & 0x80) != 0);

    return result;
  }

  private record PingResult(int onlinePlayers, String rawResponse) {}

  private record DisplayServer(
      FarmConfig.FarmServerDefinition definition,
      int onlinePlayers,
      boolean maintenance,
      String motd) {}
}
