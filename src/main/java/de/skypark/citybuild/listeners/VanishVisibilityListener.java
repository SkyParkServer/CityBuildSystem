package de.skypark.citybuild.listeners;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VanishVisibilityListener implements Listener {

  private final CityBuildSystem plugin;

  public VanishVisibilityListener(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    plugin.vanishService().applyJoinVisibility(event.getPlayer());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    plugin.vanishService().clearOnQuit(event.getPlayer());
  }
}
