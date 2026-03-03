package de.skypark.citybuild.listeners;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.storage.MessagingStore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MessagingJoinListener implements Listener {

    private final CityBuildSystem plugin;

    public MessagingJoinListener(CityBuildSystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (MessagingStore.PendingMessage pending : plugin.messagingStore().pollUndelivered(player.getUniqueId())) {
            player.sendMessage(plugin.messages().color("&8[&6MSG&8] &b" + pending.fromName() + " &7-> Du&8: &f" + pending.message()));
            plugin.messagingStore().updateLast(pending.fromUuid(), player.getUniqueId());
        }
    }
}
