package de.skypark.citybuild.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/** No-op for MySQL backend (homes are loaded on demand). */
public class HomeJoinListener implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {}
}
