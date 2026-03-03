package de.skypark.citybuild.core;

import de.skypark.citybuild.CityBuildSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionChecker {

  private static final Pattern TAG_NAME_PATTERN =
      Pattern.compile("\\\"tag_name\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");
  private static final Pattern NAME_PATTERN =
      Pattern.compile("\\\"name\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

  private final CityBuildSystem plugin;

  public VersionChecker(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  public void checkAsync() {
    boolean enabled = plugin.getConfig().getBoolean("update-checker.enabled", true);
    if (!enabled) {
      return;
    }

    plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(
            plugin,
            () -> {
              try {
                checkNow();
              } catch (Exception ex) {
                plugin.getLogger().warning("Versions-Check fehlgeschlagen: " + ex.getMessage());
              }
            });
  }

  private void checkNow() throws IOException {
    String repository =
        plugin.getConfig().getString("update-checker.repository", "SkyParkServer/CityBuildSystem");
    int timeoutMs = plugin.getConfig().getInt("update-checker.timeout-ms", 5000);
    String currentVersion = plugin.getDescription().getVersion();

    String latestTag = fetchLatestReleaseTag(repository, timeoutMs);
    if (latestTag == null || latestTag.isEmpty()) {
      latestTag = fetchLatestTag(repository, timeoutMs);
    }
    if (latestTag == null || latestTag.isEmpty()) {
      plugin.getLogger().warning("Versions-Check: Keine Release/Tag-Version gefunden.");
      return;
    }

    String currentNormalized = normalizeVersion(currentVersion);
    String latestNormalized = normalizeVersion(latestTag);

    int cmp = compareVersions(currentNormalized, latestNormalized);
    if (cmp < 0) {
      plugin
          .getLogger()
          .warning(
              "Neue Version verfuegbar: "
                  + latestTag
                  + " (aktuell: "
                  + currentVersion
                  + ") - https://github.com/"
                  + repository
                  + "/releases/latest");
    } else {
      plugin
          .getLogger()
          .info(
              "Versions-Check: Du nutzt die aktuelle Version ("
                  + currentVersion
                  + ", latest: "
                  + latestTag
                  + ").");
    }
  }

  private String fetchLatestReleaseTag(String repository, int timeoutMs) throws IOException {
    String url = "https://api.github.com/repos/" + repository + "/releases/latest";
    String body = fetch(url, timeoutMs);
    if (body == null || body.isEmpty()) {
      return null;
    }
    Matcher matcher = TAG_NAME_PATTERN.matcher(body);
    return matcher.find() ? matcher.group(1) : null;
  }

  private String fetchLatestTag(String repository, int timeoutMs) throws IOException {
    String url = "https://api.github.com/repos/" + repository + "/tags";
    String body = fetch(url, timeoutMs);
    if (body == null || body.isEmpty()) {
      return null;
    }
    Matcher matcher = NAME_PATTERN.matcher(body);
    return matcher.find() ? matcher.group(1) : null;
  }

  private String fetch(String urlText, int timeoutMs) throws IOException {
    URL url = new URL(urlText);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setConnectTimeout(timeoutMs);
    connection.setReadTimeout(timeoutMs);
    connection.setRequestProperty("Accept", "application/vnd.github+json");
    connection.setRequestProperty("User-Agent", "CityBuildSystem-VersionChecker");
    connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");

    int responseCode = connection.getResponseCode();
    if (responseCode >= 200 && responseCode < 300) {
      try (InputStream input = connection.getInputStream()) {
        return readAll(input);
      }
    }

    if (responseCode == 404) {
      return null;
    }

    try (InputStream error = connection.getErrorStream()) {
      String errorBody = error == null ? "" : readAll(error);
      throw new IOException("HTTP " + responseCode + " bei GitHub API: " + errorBody);
    }
  }

  private String readAll(InputStream input) throws IOException {
    StringBuilder builder = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        builder.append(line);
      }
    }
    return builder.toString();
  }

  private String normalizeVersion(String version) {
    if (version == null || version.isBlank()) {
      return "0";
    }

    String v = version.trim().toLowerCase();
    if (v.startsWith("v")) {
      v = v.substring(1);
    }

    int dash = v.indexOf('-');
    if (dash >= 0) {
      v = v.substring(0, dash);
    }

    return v.replaceAll("[^0-9.]", "");
  }

  private int compareVersions(String a, String b) {
    String[] left = a.split("\\.");
    String[] right = b.split("\\.");
    int max = Math.max(left.length, right.length);

    for (int i = 0; i < max; i++) {
      int lv = i < left.length ? parsePart(left[i]) : 0;
      int rv = i < right.length ? parsePart(right[i]) : 0;
      if (lv != rv) {
        return Integer.compare(lv, rv);
      }
    }
    return 0;
  }

  private int parsePart(String part) {
    if (part == null || part.isEmpty()) {
      return 0;
    }
    try {
      return Integer.parseInt(part);
    } catch (NumberFormatException ignored) {
      return 0;
    }
  }
}
