package de.skypark.citybuild.commands;

import de.skypark.citybuild.CityBuildSystem;
import de.skypark.citybuild.storage.BankStore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BankCommand implements CommandExecutor, TabCompleter {

    private final CityBuildSystem plugin;
    private final BankStore bank;

    public BankCommand(CityBuildSystem plugin, BankStore bank) {
        this.plugin = plugin;
        this.bank = bank;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.messages().error(sender, "Nur Spieler koennen diesen Befehl nutzen.");
            return true;
        }

        if (args.length == 0) {
            if (!player.hasPermission("cb.bank.use")) {
                plugin.messages().error(player, "Du hast dazu keine Rechte!");
                return true;
            }
            plugin.messages().message(player, "&7Bankkonto: &e$" + (int) bank.balance(player));
            return true;
        }

        String action = args[0].toLowerCase();
        if (args.length < 2) {
            plugin.messages().error(player, "Nutze: /bank <einzahlen|auszahlen> <Betrag>");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            plugin.messages().error(player, "Ungueltiger Betrag.");
            return true;
        }
        if (amount <= 0) {
            plugin.messages().error(player, "Der Betrag muss groesser als 0 sein.");
            return true;
        }

        if ("einzahlen".equals(action)) {
            if (!player.hasPermission("cb.bank.einzahlen.use")) {
                plugin.messages().error(player, "Du hast dazu keine Rechte!");
                return true;
            }
            if (!plugin.money().takeMoney(player, amount)) {
                plugin.messages().error(player, "Du hast nicht genug Geld.");
                return true;
            }
            bank.deposit(player, amount);
            plugin.messages().success(player, "Du hast &e$" + amount + "&a eingezahlt.");
            return true;
        }

        if ("auszahlen".equals(action)) {
            if (!player.hasPermission("cb.bank.auszahlen.use")) {
                plugin.messages().error(player, "Du hast dazu keine Rechte!");
                return true;
            }
            if (!bank.withdraw(player, amount)) {
                plugin.messages().error(player, "Nicht genug Guthaben auf dem Bankkonto.");
                return true;
            }
            plugin.money().addMoney(player, amount);
            plugin.messages().success(player, "Du hast &e$" + amount + "&a ausgezahlt.");
            return true;
        }

        plugin.messages().error(player, "Nutze: /bank <einzahlen|auszahlen> <Betrag>");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = List.of("einzahlen", "auszahlen");
            List<String> result = new ArrayList<>();
            String prefix = args[0].toLowerCase();
            for (String option : options) {
                if (option.startsWith(prefix)) {
                    result.add(option);
                }
            }
            return result;
        }
        return List.of();
    }
}
