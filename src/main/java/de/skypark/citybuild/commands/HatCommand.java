package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HatCommand implements CommandExecutor {

  private final CityBuildSystem plugin;

  public HatCommand(CityBuildSystem plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
      return true;
    }
    if (!player.hasPermission("cb.hat.use")) {
      plugin.messages().error(player, "Du hast dazu keine Rechte!");
      return true;
    }

    ItemStack hand = player.getInventory().getItemInMainHand();
    if (hand == null || hand.getType().isAir()) {
      plugin.messages().error(player, "Du musst ein Item in der Hand halten.");
      return true;
    }

    ItemStack currentHelmet = player.getInventory().getHelmet();
    ItemStack newHelmet = hand.clone();
    newHelmet.setAmount(1);

    player.getInventory().setHelmet(newHelmet);

    int remaining = hand.getAmount() - 1;
    if (remaining <= 0) {
      player.getInventory().setItemInMainHand(null);
    } else {
      hand.setAmount(remaining);
      player.getInventory().setItemInMainHand(hand);
    }

    if (currentHelmet != null && !currentHelmet.getType().isAir()) {
      java.util.HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(currentHelmet);
      for (ItemStack item : overflow.values()) {
        player.getWorld().dropItemNaturally(player.getLocation(), item);
      }
    }

    plugin.messages().success(player, "Du traegst dein Item jetzt als Helm.");
    return true;
  }
}
