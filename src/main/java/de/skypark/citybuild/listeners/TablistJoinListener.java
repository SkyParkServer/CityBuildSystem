package de.skypark.citybuild.listeners;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TablistJoinListener implements Listener {

    private final CityBuildSystem plugin;

    public TablistJoinListener(CityBuildSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String format = plugin.messages().color("&7" + player.getName());
        player.setPlayerListName(format);
    }
}
