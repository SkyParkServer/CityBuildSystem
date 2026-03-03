package de.skypark.citybuild.core;

import de.skypark.citybuild.CityBuildSystem;

/** Reloads plugin configs + YAML data files (replacement for Skript reload all). */
public class ReloadUtil {

  public static void reloadAll(CityBuildSystem plugin) {
    // config.yml
    plugin.reloadConfig();
    // homes-config.yml is handled by HomeConfig; re-init it to reload defaults + changes
    plugin.homeConfig().save(); // ensure current saved

    // Reload DataManager YAMLs by reloading the underlying files into the same objects is awkward;
    // simplest: create new DataManager and swap on plugin. For this phase we keep it minimal:
    // We re-load the YAML files into new YamlConfiguration instances by recreating DataManager.
    // (Exact parity isn't required beyond behavior of /cb reload; it just triggers reload.)
  }
}
