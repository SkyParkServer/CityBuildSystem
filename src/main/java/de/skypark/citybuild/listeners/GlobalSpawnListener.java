package de.skypark.citybuild.listeners;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GlobalSpawnListener implements Listener {

  private final CityBuildSystem plugin;

  public GlobalSpawnListener(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    if (!plugin.spawnManager().teleportOnJoinEnabled()) {
      return;
    }

    Player player = event.getPlayer();
    Location spawn = plugin.spawnManager().getSpawn();
    if (spawn != null) {
      player.teleport(spawn);
    }
  }
}
