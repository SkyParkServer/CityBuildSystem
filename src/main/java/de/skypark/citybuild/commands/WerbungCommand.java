package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.storage.WerbungStore;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WerbungCommand implements CommandExecutor {

  private static final long COOLDOWN_MS = 24L * 60L * 60L * 1000L;

  private final CityBuildSystem plugin;
  private final WerbungStore werbungStore;

  public WerbungCommand(CityBuildSystem plugin, WerbungStore werbungStore) {
    this.plugin = plugin;
    this.werbungStore = werbungStore;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }
    if (!player.hasPermission("cb.werbung.use")) {
      plugin.messages().error(player, "Du hast dazu keine Rechte!");
      return true;
    }
    if (args.length == 0) {
      plugin.messages().error(player, "Nutze: /werbung <Nachricht>");
      return true;
    }
    if (!canSendWerbungHere(player)) {
      return true;
    }

    long now = System.currentTimeMillis();
    long last = werbungStore.getLastUsed(player.getUniqueId());
    if (last > 0 && now - last < COOLDOWN_MS) {
      long remaining = (COOLDOWN_MS - (now - last)) / 1000L;
      long hours = remaining / 3600L;
      long minutes = (remaining % 3600L) / 60L;
      plugin
          .messages()
          .error(
              player, "Du kannst erst in " + hours + "h " + minutes + "m wieder Werbung senden.");
      return true;
    }

    String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
    String serverName = plugin.getConfig().getString("server-name", "citybuild-1");
    werbungStore.setLastUsed(player.getUniqueId(), serverName, player.getLocation());

    String border = "§6§lSkyPark §8» ------------------------------------------";
    String empty = "§6§lSkyPark §8»";
    String msgLine = "§6§lSkyPark §8» §7" + message;

    for (Player online : Bukkit.getOnlinePlayers()) {
      online.sendMessage(border);
      online.sendMessage(empty);
      online.sendMessage(msgLine);
      online.sendMessage(empty);

      TextComponent component =
          new TextComponent("§6§lSkyPark §8» §bTeleportation §7(klickbar zum Plot)");
      component.setClickEvent(
          new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/werbungtp " + player.getUniqueId()));
      online.spigot().sendMessage(new ComponentBuilder(component).create());

      online.sendMessage(empty);
      online.sendMessage(border);
    }

    plugin.messages().success(player, "Werbung wurde gesendet.");
    return true;
  }

  private boolean canSendWerbungHere(Player player) {
    if (Bukkit.getPluginManager().getPlugin("PlotSquared") == null) {
      plugin
          .messages()
          .error(player, "PlotSquared wurde nicht gefunden. Werbung ist derzeit deaktiviert.");
      return false;
    }

    try {
      Class<?> plotPlayerClass = Class.forName("com.plotsquared.core.player.PlotPlayer");
      Method fromMethod = plotPlayerClass.getMethod("from", Object.class);
      Object plotPlayer = fromMethod.invoke(null, player);

      Method getCurrentPlot = plotPlayerClass.getMethod("getCurrentPlot");
      Object plot = getCurrentPlot.invoke(plotPlayer);
      if (plot == null) {
        plugin
            .messages()
            .error(player, "Du kannst Werbung nur auf deinem oder vertrauten Plot senden.");
        return false;
      }

      Method getUuidMethod = plotPlayerClass.getMethod("getUUID");
      UUID uuid = (UUID) getUuidMethod.invoke(plotPlayer);

      Method isOwnerMethod = plot.getClass().getMethod("isOwner", UUID.class);
      Method isAddedMethod = plot.getClass().getMethod("isAdded", UUID.class);
      boolean owner = (boolean) isOwnerMethod.invoke(plot, uuid);
      boolean trustedOrAdded = (boolean) isAddedMethod.invoke(plot, uuid);

      if (!owner && !trustedOrAdded) {
        plugin
            .messages()
            .error(player, "Du kannst Werbung nur auf deinem oder vertrauten Plot senden.");
        return false;
      }
      return true;
    } catch (Exception ex) {
      plugin
          .getLogger()
          .warning("PlotSquared-Pruefung fuer /werbung fehlgeschlagen: " + ex.getMessage());
      plugin
          .messages()
          .error(player, "Werbung konnte nicht geprueft werden. Versuche es spaeter erneut.");
      return false;
    }
  }
}
