package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HealCommand implements CommandExecutor, TabCompleter {

    private final CityBuildSystem plugin;

    public HealCommand(CityBuildSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (!sender.hasPermission("cb.heal.other.use")) {
                plugin.messages().error(sender, "Du hast dazu keine Rechte!");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                plugin.messages().error(sender, "Spieler ist nicht online.");
                return true;
            }
            heal(target);
            plugin.messages().success(sender, "Du hast &e" + target.getName() + "&a geheilt.");
            plugin.messages().success(target, "Du wurdest geheilt.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            plugin.messages().error(sender, "Nutze: /heal <Spieler>");
            return true;
        }
        if (!player.hasPermission("cb.heal.use")) {
            plugin.messages().error(player, "Du hast dazu keine Rechte!");
            return true;
        }

        heal(player);
        plugin.messages().success(player, "Du bist jetzt voll geheilt.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return List.of();
        }
        String prefix = args[0].toLowerCase();
        List<String> names = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getName().toLowerCase().startsWith(prefix)) {
                names.add(online.getName());
            }
        }
        return names;
    }

    private void heal(Player target) {
        target.setHealth(target.getMaxHealth());
        target.setFireTicks(0);
    }
}
