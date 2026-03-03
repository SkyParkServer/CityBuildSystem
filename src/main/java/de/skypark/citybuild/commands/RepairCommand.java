package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class RepairCommand implements CommandExecutor {
  private final CityBuildSystem plugin;

  public RepairCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }

    if (!player.hasPermission("cb.repair.use")) {
      plugin.messages().error(player, plugin.settings().noPermissionMessage());
      return true;
    }

    ItemStack tool = player.getInventory().getItemInMainHand();
    if (tool == null || tool.getType().isAir()) {
      plugin.messages().error(player, "Du musst zuerst ein Item in der Hand halten.");
      return true;
    }

    if (tool.getItemMeta() instanceof Damageable dmg) {
      dmg.setDamage(0);
      tool.setItemMeta(dmg);
    }

    player.getInventory().setItemInMainHand(tool);
    plugin.messages().success(player, "Dein Item wurde vollstaendig repariert.");
    return true;
  }
}
