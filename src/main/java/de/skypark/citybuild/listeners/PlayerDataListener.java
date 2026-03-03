package de.skypark.citybuild.listeners;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.storage.PlayerDataStore;
import de.skypark.citybuild.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/** Mirrors 10_core_playerdata.sk events: - on first join - on join - on quit */
public class PlayerDataListener implements Listener {

  private final CityBuildSystem plugin;
  private final PlayerDataStore playerData;

  public PlayerDataListener(CityBuildSystem plugin, PlayerDataStore playerData) {
    this.plugin = plugin;
    this.playerData = playerData;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    // on first join:
    if (!player.hasPlayedBefore()) {
      playerData.ensureFirstJoin(player);

      // if {cb.spawn.location} is set: wait 10 ticks; teleport player to it
      String locText = plugin.globals().spawnLocationText();
      Location spawn = LocationUtil.textToLoc(locText);
      if (spawn != null) {
        plugin
            .getServer()
            .getScheduler()
            .runTaskLater(
                plugin,
                () -> {
                  // Player might have disconnected, but Paper keeps Player object until quit. Still
                  // safe check.
                  if (player.isOnline()) player.teleport(spawn);
                },
                10L);
      }
    }

    // on join:
    playerData.setLastJoin(player);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    playerData.setLastSeen(event.getPlayer());
  }
}
