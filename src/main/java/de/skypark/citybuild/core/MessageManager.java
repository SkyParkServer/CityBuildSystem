package de.skypark.citybuild.core;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/** Mirrors 01_utils_messages.sk: cbColor, cbMessage, cbSuccess, cbError, cbDebug */
public class MessageManager {

  private final CityBuildSystem plugin;

  public MessageManager(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  public String color(String message) {
    return ChatColor.translateAlternateColorCodes('&', message == null ? "" : message);
  }

  public void message(CommandSender target, String message) {
    String prefix = plugin.settings().prefix();
    target.sendMessage(color(prefix + " " + message));
  }

  public void success(CommandSender target, String message) {
    String prefix = plugin.settings().prefix();
    target.sendMessage(color(prefix + " &a" + message));
  }

  public void error(CommandSender target, String message) {
    String prefix = plugin.settings().prefix();
    target.sendMessage(color(prefix + " &c" + message));
  }

  public void debug(String message) {
    if (!plugin.globals().debugEnabled()) return;
    Bukkit.getConsoleSender().sendMessage(color("&8[&6CityBuild-Debug&8]&7 " + message));
  }
}
