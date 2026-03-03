package de.skypark.citybuild.core;

import de.skypark.citybuild.CityBuildSystem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishService {

  private final CityBuildSystem plugin;
  private final Set<UUID> vanished = new HashSet<>();

  public VanishService(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  public boolean isVanished(UUID uuid) {
    return vanished.contains(uuid);
  }

  public boolean isVisibleTo(CommandSender sender, Player target) {
    if (!isVanished(target.getUniqueId())) {
      return true;
    }
    if (sender instanceof Player viewer) {
      return viewer.getUniqueId().equals(target.getUniqueId());
    }
    return false;
  }

  public int visibleOnlineCount(CommandSender sender) {
    int count = 0;
    for (Player online : Bukkit.getOnlinePlayers()) {
      if (isVisibleTo(sender, online)) {
        count++;
      }
    }
    return count;
  }

  public List<String> visibleOnlineNames(CommandSender sender, String prefix) {
    String normalizedPrefix = prefix == null ? "" : prefix.toLowerCase();
    List<String> names = new ArrayList<>();
    for (Player online : Bukkit.getOnlinePlayers()) {
      if (!isVisibleTo(sender, online)) {
        continue;
      }
      String name = online.getName();
      if (name.toLowerCase().startsWith(normalizedPrefix)) {
        names.add(name);
      }
    }
    return names;
  }

  public Player findVisiblePlayer(CommandSender sender, String name) {
    Player player = Bukkit.getPlayerExact(name);
    if (player == null) {
      return null;
    }
    return isVisibleTo(sender, player) ? player : null;
  }

  public boolean setVanished(Player target, boolean shouldBeVanished) {
    UUID uuid = target.getUniqueId();
    boolean currentlyVanished = vanished.contains(uuid);
    if (currentlyVanished == shouldBeVanished) {
      return currentlyVanished;
    }

    if (shouldBeVanished) {
      vanished.add(uuid);
      target.setInvisible(true);
      hideFromAll(target);
      return true;
    }

    vanished.remove(uuid);
    target.setInvisible(false);
    showToAll(target);
    return false;
  }

  public boolean toggle(Player target) {
    return setVanished(target, !isVanished(target.getUniqueId()));
  }

  public void applyJoinVisibility(Player joinedPlayer) {
    UUID joinedUuid = joinedPlayer.getUniqueId();
    for (UUID vanishedUuid : vanished) {
      if (vanishedUuid.equals(joinedUuid)) {
        continue;
      }
      Player vanishedPlayer = Bukkit.getPlayer(vanishedUuid);
      if (vanishedPlayer != null) {
        joinedPlayer.hidePlayer(plugin, vanishedPlayer);
      }
    }
  }

  public void clearOnQuit(Player player) {
    vanished.remove(player.getUniqueId());
  }

  private void hideFromAll(Player target) {
    UUID targetUuid = target.getUniqueId();
    for (Player online : Bukkit.getOnlinePlayers()) {
      if (online.getUniqueId().equals(targetUuid)) {
        continue;
      }
      online.hidePlayer(plugin, target);
    }
  }

  private void showToAll(Player target) {
    UUID targetUuid = target.getUniqueId();
    for (Player online : Bukkit.getOnlinePlayers()) {
      if (online.getUniqueId().equals(targetUuid)) {
        continue;
      }
      online.showPlayer(plugin, target);
    }
  }
}
