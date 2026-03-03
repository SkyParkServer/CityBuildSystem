package de.skypark.citybuild;

import de.skypark.citybuild.commands.*;
import de.skypark.citybuild.core.CityBuildSettings;
import de.skypark.citybuild.core.HomeConfig;
import de.skypark.citybuild.core.HomeService;
import de.skypark.citybuild.core.MessageManager;
import de.skypark.citybuild.core.SpawnManager;
import de.skypark.citybuild.listeners.GlobalSpawnListener;
import de.skypark.citybuild.listeners.HomeGuiListener;
import de.skypark.citybuild.listeners.HomeJoinListener;
import de.skypark.citybuild.listeners.InvseeReadOnlyListener;
import de.skypark.citybuild.listeners.LuckPermsUpdateListener;
import de.skypark.citybuild.listeners.MessagingJoinListener;
import de.skypark.citybuild.listeners.PlayerDataListener;
import de.skypark.citybuild.listeners.RainbowArmorListener;
import de.skypark.citybuild.listeners.SharedEnderChestListener;
import de.skypark.citybuild.listeners.TablistJoinListener;
import de.skypark.citybuild.storage.*;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Accessors(fluent = true)
public class CityBuildSystem extends JavaPlugin {

  private static CityBuildSystem instance;

  private CityBuildSettings settings;
  private MessageManager messages;
  private DataManager data;
  private GlobalState globals;
  private MoneyStore money;
  private CooldownStore cooldowns;
  private PlayerDataStore playerData;
  private SpawnManager spawnManager;

  private EnderChestStore enderChestStore;
  private WarpStore warpStore;

  private MessagingStore messagingStore;
  private PlayerLookupStore playerLookup;

  private CrystalsStore crystals;
  private BankStore bank;
  private WerbungStore werbungStore;

  private HomeConfig homeConfig;
  private HomeService homes;

  @Override
  public void onEnable() {
    instance = this;

    saveDefaultConfig();

    this.settings = new CityBuildSettings(this);
    this.data = new DataManager(this);
    this.globals = new GlobalState(data, settings);
    this.messages = new MessageManager(this);
    this.money = new MoneyStore(data);
    this.cooldowns = new CooldownStore(data);
    this.playerData = new PlayerDataStore(data, settings);
    this.spawnManager = new SpawnManager(this);

    this.enderChestStore = new EnderChestStore(data);
    this.warpStore = new WarpStore(data);

    this.messagingStore = new MessagingStore(data);
    this.playerLookup = new PlayerLookupStore(data);

    this.crystals = new CrystalsStore(data);
    this.bank = new BankStore(data);
    this.werbungStore = new WerbungStore(data);

    this.homeConfig = new HomeConfig(this);
    this.homes = new HomeService(this, homeConfig);

    // Events
    getServer().getPluginManager().registerEvents(new PlayerDataListener(this, playerData), this);
    getServer().getPluginManager().registerEvents(new SharedEnderChestListener(this), this);
    getServer().getPluginManager().registerEvents(new HomeJoinListener(), this);
    getServer().getPluginManager().registerEvents(new HomeGuiListener(this, homes), this);
    getServer().getPluginManager().registerEvents(new InvseeReadOnlyListener(this), this);
    getServer().getPluginManager().registerEvents(new RainbowArmorListener(this), this);
    getServer().getPluginManager().registerEvents(new GlobalSpawnListener(this), this);
    getServer().getPluginManager().registerEvents(new MessagingJoinListener(this), this);
    getServer().getPluginManager().registerEvents(new TablistJoinListener(this), this);
    getServer().getPluginManager().registerEvents(new LuckPermsUpdateListener(this), this);

    // Commands - spawn
    registerCommand("setspawn", new SetSpawnCommand(this));
    registerCommand("spawn", new SpawnCommand(this));

    // Commands - homes
    registerCommand("home", new HomeCommand(this, homes));
    registerCommand("homes", new HomesCommand(this, homes));
    registerCommand("sethome", new SetHomeCommand(this, homes));
    registerCommand("delhome", new DelHomeCommand(this, homes));

    // Commands - economy
    registerCommand("balance", new BalanceCommand(this));
    registerCommand("eco", new EcoCommand(this), new EcoTabCompleter());

    // Commands - QoL
    registerCommand("workbench", new WorkbenchCommand(this));
    registerCommand("anvil", new AnvilCommand(this));
    registerCommand("sign", new SignCommand(this));
    registerCommand("repair", new RepairCommand(this));
    registerCommand("wetter", new WetterCommand(this), new WetterTabCompleter());

    // Commands - extras
    EnderChestCommand ec = new EnderChestCommand(this);
    registerCommand("enderchest", ec);
    registerCommand("ec", ec);
    registerCommand("speed", new SpeedCommand(this));
    registerCommand("hat", new HatCommand(this));
    registerCommand("setwarp", new SetWarpCommand(this));
    registerCommand("delwarp", new DelWarpCommand(this));
    registerCommand("warp", new WarpCommand(this));
    registerCommand("warps", new WarpsCommand(this));

    // Commands - messaging
    MsgCommand msgCmd = new MsgCommand(this);
    registerCommand("msg", msgCmd, msgCmd);
    registerCommand("r", new ReplyCommand(this));
    registerCommand("msgtoggle", new MsgToggleCommand(this));

    // Commands - admin utils
    InvseeCommand invsee = new InvseeCommand(this);
    registerCommand("invsee", invsee, invsee);
    registerCommand("near", new NearCommand(this));

    FeedCommand feed = new FeedCommand(this);
    registerCommand("feed", feed, feed);

    HealCommand heal = new HealCommand(this);
    registerCommand("heal", heal, heal);

    registerCommand("kristalle", new KristalleCommand(this, crystals));

    BankCommand bankCommand = new BankCommand(this, bank);
    registerCommand("bank", bankCommand, bankCommand);

    registerCommand("werbung", new WerbungCommand(this, werbungStore));
    registerCommand("werbungtp", new WerbungTpCommand(this, werbungStore));
    registerCommand("regenbogen", new RegenbogenCommand(this));

    // Commands - admin
    registerCommand("cb", new CbCommand(this), new CbTabCompleter());
  }

  @Override
  public void onDisable() {
    data.saveAll();
  }

  public static CityBuildSystem getInstance() {
    return instance;
  }

  public void registerInvseeViewer(java.util.UUID viewer, java.util.UUID target) {
    InvseeReadOnlyListener.setViewing(viewer, target);
  }

  public void clearInvseeViewer(java.util.UUID viewer) {
    InvseeReadOnlyListener.clearViewing(viewer);
  }

  public void setRainbowArmorEnabled(java.util.UUID uuid, boolean enabled) {
    RainbowArmorListener.setEnabled(uuid, enabled);
  }

  public boolean isRainbowArmorEnabled(java.util.UUID uuid) {
    return RainbowArmorListener.isEnabled(uuid);
  }

  private void registerCommand(String name, CommandExecutor executor) {
    PluginCommand command = getCommand(name);
    if (command != null) {
      command.setExecutor(executor);
    }
  }

  private void registerCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
    PluginCommand command = getCommand(name);
    if (command != null) {
      command.setExecutor(executor);
      command.setTabCompleter(tabCompleter);
    }
  }
}
