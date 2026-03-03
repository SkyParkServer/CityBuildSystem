package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.util.DateFormatUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SignCommand implements CommandExecutor {
  private final CityBuildSystem plugin;

  public SignCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }

    if (!player.hasPermission("cb.sign.use")) {
      plugin.messages().error(player, plugin.settings().noPermissionMessage());
      return true;
    }

    ItemStack tool = player.getInventory().getItemInMainHand();
    if (tool == null || tool.getType().isAir()) {
      plugin.messages().error(player, "Du musst zuerst ein Item in der Hand halten.");
      return true;
    }

    ItemMeta meta = tool.getItemMeta();
    List<String> lore =
        meta != null && meta.getLore() != null
            ? new ArrayList<>(meta.getLore())
            : new ArrayList<>();

    String signaturePrefix = plugin.messages().color("&7Signiert von &b");
    String signatureByPlayerPrefix =
        plugin.messages().color("&7Signiert von &b" + player.getName() + " &7am &b");

    boolean signedByAny = false;
    boolean signedByPlayer = false;

    for (String line : lore) {
      if (line != null && line.startsWith(signaturePrefix)) signedByAny = true;
      if (line != null && line.startsWith(signatureByPlayerPrefix)) signedByPlayer = true;
    }

    if (signedByPlayer) {
      plugin.messages().error(player, "Du hast dieses Item bereits signiert.");
      return true;
    }

    if (signedByAny && !player.hasPermission("cb.sign.team")) {
      plugin.messages().error(player, "Dieses Item ist bereits signiert.");
      return true;
    }

    String dateText = DateFormatUtil.now();
    lore.add("");
    lore.add(
        plugin.messages().color("&7Signiert von &b" + player.getName() + " &7am &b" + dateText));

    if (meta != null) {
      meta.setLore(lore);
      tool.setItemMeta(meta);
    }
    player.getInventory().setItemInMainHand(tool);

    plugin.messages().success(player, "Item wurde signiert.");
    return true;
  }
}
