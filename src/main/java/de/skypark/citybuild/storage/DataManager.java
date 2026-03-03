package de.skypark.citybuild.storage;

import de.skypark.citybuild.CityBuildSystem;

public class DataManager {

  private final DatabaseManager database;

  public DataManager(CityBuildSystem plugin) {
    this.database = new DatabaseManager(plugin);
    this.database.initSchema();
  }

  public DatabaseManager db() {
    return database;
  }

  public void saveAll() {
    // No-op for MySQL backend
  }
}
