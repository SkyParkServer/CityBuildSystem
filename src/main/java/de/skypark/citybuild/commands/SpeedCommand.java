package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {

    private final CityBuildSystem plugin;

    public SpeedCommand(CityBuildSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
            return true;
        }
        if (!player.hasPermission("cb.speed.use")) {
            plugin.messages().error(player, "Du hast dazu keine Rechte!");
            return true;
        }

        int level = 2;
        if (args.length >= 1) {
            try {
                level = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                plugin.messages().error(player, "Nutze: /speed <1-10>");
                return true;
            }
        }

        if (level < 1 || level > 10) {
            plugin.messages().error(player, "Nutze: /speed <1-10>");
            return true;
        }

        float bukkitSpeed = (float) ((level - 1) / 9.0);
        if (player.isFlying()) {
            player.setFlySpeed(bukkitSpeed);
        } else {
            player.setWalkSpeed(bukkitSpeed);
        }
        plugin.messages().success(player, "Deine Geschwindigkeit ist jetzt &e" + level + "&a.");
        return true;
    }
}
