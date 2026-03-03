package de.skypark.citybuild.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/** Mirrors cbLocToText from 02_utils_core.sk and adds inverse. Format: world|x|y|z|yaw|pitch */
public class LocationUtil {

  public static String locToText(Location loc) {
    if (loc == null || loc.getWorld() == null) return "";
    return loc.getWorld().getName()
        + "|"
        + loc.getX()
        + "|"
        + loc.getY()
        + "|"
        + loc.getZ()
        + "|"
        + loc.getYaw()
        + "|"
        + loc.getPitch();
  }

  public static Location textToLoc(String text) {
    if (text == null || text.isEmpty()) return null;
    String[] parts = text.split("\\|");
    if (parts.length < 6) return null;

    World world = Bukkit.getWorld(parts[0]);
    if (world == null) return null;

    try {
      double x = Double.parseDouble(parts[1]);
      double y = Double.parseDouble(parts[2]);
      double z = Double.parseDouble(parts[3]);
      float yaw = Float.parseFloat(parts[4]);
      float pitch = Float.parseFloat(parts[5]);
      return new Location(world, x, y, z, yaw, pitch);
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
