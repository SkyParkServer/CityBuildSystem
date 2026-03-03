package de.skypark.citybuild;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.skypark.citybuild.commands.*;
import de.skypark.citybuild.core.CityBuildSettings;
import de.skypark.citybuild.core.HomeConfig;
import de.skypark.citybuild.core.HomeService;
import de.skypark.citybuild.core.MessageManager;
import de.skypark.citybuild.core.SpawnManager;
import de.skypark.citybuild.core.TresorService;
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
import de.skypark.citybuild.listeners.TresorListener;
import de.skypark.citybuild.storage.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
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
  private TresorStore tresorStore;
  private WarpStore warpStore;

  private MessagingStore messagingStore;
  private PlayerLookupStore playerLookup;

  private CrystalsStore crystals;
  private BankStore bank;
  private WerbungStore werbungStore;

  private HomeConfig homeConfig;
  private HomeService homes;
  private TresorService tresorService;
  private final Map<String, CommandBinding> commandBindings = new LinkedHashMap<>();

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
    this.tresorStore = new TresorStore(data);
    this.warpStore = new WarpStore(data);

    this.messagingStore = new MessagingStore(data);
    this.playerLookup = new PlayerLookupStore(data);

    this.crystals = new CrystalsStore(data);
    this.bank = new BankStore(data);
    this.werbungStore = new WerbungStore(data);

    this.homeConfig = new HomeConfig(this);
    this.homes = new HomeService(this, homeConfig);
    this.tresorService = new TresorService(this, tresorStore);

    // Events
    getServer().getPluginManager().registerEvents(new PlayerDataListener(this, playerData), this);
    getServer().getPluginManager().registerEvents(new SharedEnderChestListener(this), this);
    getServer().getPluginManager().registerEvents(new TresorListener(tresorService), this);
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
    registerCommand("wetter", new WetterCommand(this));

    // Commands - extras
    EnderChestCommand ec = new EnderChestCommand(this);
    registerCommand("enderchest", ec);
    registerCommand("ec", ec);
    registerCommand("tresor", new TresorCommand(this, tresorService));
    registerCommand("speed", new SpeedCommand(this));
    registerCommand("hat", new HatCommand(this));
    registerCommand("setwarp", new SetWarpCommand(this));
    registerCommand("delwarp", new DelWarpCommand(this));
    registerCommand("warp", new WarpCommand(this));
    registerCommand("warps", new WarpsCommand(this));

    // Commands - messaging
    registerCommand("msg", new MsgCommand(this));
    registerCommand("r", new ReplyCommand(this));
    registerCommand("msgtoggle", new MsgToggleCommand(this));

    // Commands - admin utils
    registerCommand("invsee", new InvseeCommand(this));
    registerCommand("near", new NearCommand(this));

    registerCommand("feed", new FeedCommand(this));

    registerCommand("heal", new HealCommand(this));

    registerCommand("kristalle", new KristalleCommand(this, crystals));

    BankCommand bankCommand = new BankCommand(this, bank);
    registerCommand("bank", bankCommand, bankCommand);

    registerCommand("werbung", new WerbungCommand(this, werbungStore));
    registerCommand("werbungtp", new WerbungTpCommand(this, werbungStore));
    registerCommand("regenbogen", new RegenbogenCommand(this));

    // Commands - admin
    registerCommand("cb", new CbCommand(this), new CbTabCompleter());

    registerBrigadierCommands();
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
      TabCompleter tabCompleter =
          executor instanceof TabCompleter tc ? tc : command.getTabCompleter();
      if (tabCompleter != null) {
        command.setTabCompleter(tabCompleter);
      }
      commandBindings.put(
          command.getName().toLowerCase(), new CommandBinding(command, executor, tabCompleter));
    }
  }

  private void registerCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
    PluginCommand command = getCommand(name);
    if (command != null) {
      command.setExecutor(executor);
      command.setTabCompleter(tabCompleter);
      commandBindings.put(
          command.getName().toLowerCase(), new CommandBinding(command, executor, tabCompleter));
    }
  }

  private void registerBrigadierCommands() {
    this.getLifecycleManager()
        .registerEventHandler(
            LifecycleEvents.COMMANDS,
            event -> {
              for (CommandBinding binding : commandBindings.values()) {
                event
                    .registrar()
                    .register(
                        buildBrigadierCommand(binding),
                        binding.command().getDescription(),
                        binding.command().getAliases());
              }
            });
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildBrigadierCommand(
      CommandBinding binding) {
    return switch (binding.command().getName().toLowerCase()) {
      case "wetter" -> buildWetterBrigadier(binding);
      case "speed" -> buildSpeedBrigadier(binding);
      case "msg" -> buildMsgBrigadier(binding);
      case "r" -> buildReplyBrigadier(binding);
      case "feed" -> buildFeedBrigadier(binding);
      case "heal" -> buildHealBrigadier(binding);
      case "invsee" -> buildInvseeBrigadier(binding);
      case "bank" -> buildBankBrigadier(binding);
      case "warp" -> buildWarpBrigadier(binding);
      case "setwarp" -> buildSetWarpBrigadier(binding);
      case "delwarp" -> buildDelWarpBrigadier(binding);
      case "home" -> buildHomeBrigadier(binding);
      case "sethome" -> buildSetHomeBrigadier(binding);
      case "delhome" -> buildDelHomeBrigadier(binding);
      case "setspawn" -> buildSetSpawnBrigadier(binding);
      case "spawn" -> buildSpawnBrigadier(binding);
      case "balance" -> buildBalanceBrigadier(binding);
      case "eco" -> buildEcoBrigadier(binding);
      case "kristalle" -> buildKristalleBrigadier(binding);
      case "cb" -> buildCbBrigadier(binding);
      case "werbung" -> buildWerbungBrigadier(binding);
      case "werbungtp" -> buildWerbungTpBrigadier(binding);
      default -> buildGenericBrigadier(binding);
    };
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildGenericBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .executes(ctx -> executeBrigadier(ctx, binding, new String[0]))
        .then(
            Commands.argument("args", StringArgumentType.greedyString())
                .suggests((ctx, builder) -> suggestBrigadier(ctx, builder, binding))
                .executes(ctx -> executeBrigadier(ctx, binding, splitArgs(getString(ctx, "args")))))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildWetterBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .executes(ctx -> executeBrigadier(ctx, binding, new String[0]))
        .then(
            Commands.literal("regen")
                .executes(ctx -> executeBrigadier(ctx, binding, new String[] {"regen"})))
        .then(
            Commands.literal("gewitter")
                .executes(ctx -> executeBrigadier(ctx, binding, new String[] {"gewitter"})))
        .then(
            Commands.literal("sonne")
                .executes(ctx -> executeBrigadier(ctx, binding, new String[] {"sonne"})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildSpeedBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .executes(ctx -> executeBrigadier(ctx, binding, new String[0]))
        .then(
            Commands.argument("level", IntegerArgumentType.integer(1, 10))
                .executes(
                    ctx ->
                        executeBrigadier(
                            ctx, binding, new String[] {String.valueOf(getInteger(ctx, "level"))})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildMsgBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(
            Commands.argument("spieler", StringArgumentType.word())
                .suggests((ctx, builder) -> suggestOnlinePlayers(builder))
                .executes(
                    ctx -> executeBrigadier(ctx, binding, new String[] {getString(ctx, "spieler")}))
                .then(
                    Commands.argument("nachricht", StringArgumentType.greedyString())
                        .executes(
                            ctx ->
                                executeBrigadier(
                                    ctx,
                                    binding,
                                    new String[] {
                                      getString(ctx, "spieler"), getString(ctx, "nachricht")
                                    }))))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildReplyBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(
            Commands.argument("nachricht", StringArgumentType.greedyString())
                .executes(
                    ctx ->
                        executeBrigadier(ctx, binding, new String[] {getString(ctx, "nachricht")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildFeedBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .executes(ctx -> executeBrigadier(ctx, binding, new String[0]))
        .then(
            Commands.argument("spieler", StringArgumentType.word())
                .suggests((ctx, builder) -> suggestOnlinePlayers(builder))
                .executes(
                    ctx ->
                        executeBrigadier(ctx, binding, new String[] {getString(ctx, "spieler")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildHealBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .executes(ctx -> executeBrigadier(ctx, binding, new String[0]))
        .then(
            Commands.argument("spieler", StringArgumentType.word())
                .suggests((ctx, builder) -> suggestOnlinePlayers(builder))
                .executes(
                    ctx ->
                        executeBrigadier(ctx, binding, new String[] {getString(ctx, "spieler")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildInvseeBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(
            Commands.argument("spieler", StringArgumentType.word())
                .suggests((ctx, builder) -> suggestOnlinePlayers(builder))
                .executes(
                    ctx ->
                        executeBrigadier(ctx, binding, new String[] {getString(ctx, "spieler")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildBankBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .executes(ctx -> executeBrigadier(ctx, binding, new String[0]))
        .then(
            Commands.literal("einzahlen")
                .then(
                    Commands.argument("betrag", IntegerArgumentType.integer(1))
                        .executes(
                            ctx ->
                                executeBrigadier(
                                    ctx,
                                    binding,
                                    new String[] {
                                      "einzahlen", String.valueOf(getInteger(ctx, "betrag"))
                                    }))))
        .then(
            Commands.literal("auszahlen")
                .then(
                    Commands.argument("betrag", IntegerArgumentType.integer(1))
                        .executes(
                            ctx ->
                                executeBrigadier(
                                    ctx,
                                    binding,
                                    new String[] {
                                      "auszahlen", String.valueOf(getInteger(ctx, "betrag"))
                                    }))))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildWarpBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(
            Commands.argument("name", StringArgumentType.word())
                .suggests(this::suggestWarps)
                .executes(
                    ctx -> executeBrigadier(ctx, binding, new String[] {getString(ctx, "name")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildSetWarpBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(
            Commands.argument("name", StringArgumentType.word())
                .executes(
                    ctx -> executeBrigadier(ctx, binding, new String[] {getString(ctx, "name")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildDelWarpBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(
            Commands.argument("name", StringArgumentType.word())
                .suggests(this::suggestWarps)
                .executes(
                    ctx -> executeBrigadier(ctx, binding, new String[] {getString(ctx, "name")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildHomeBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .executes(ctx -> executeBrigadier(ctx, binding, new String[0]))
        .then(
            Commands.argument("name", StringArgumentType.word())
                .suggests(this::suggestHomes)
                .executes(
                    ctx -> executeBrigadier(ctx, binding, new String[] {getString(ctx, "name")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildSetHomeBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(
            Commands.argument("name", StringArgumentType.word())
                .executes(
                    ctx -> executeBrigadier(ctx, binding, new String[] {getString(ctx, "name")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildDelHomeBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(
            Commands.argument("name", StringArgumentType.word())
                .suggests(this::suggestHomes)
                .executes(
                    ctx -> executeBrigadier(ctx, binding, new String[] {getString(ctx, "name")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildSetSpawnBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .executes(ctx -> executeBrigadier(ctx, binding, new String[0]))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildSpawnBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .executes(ctx -> executeBrigadier(ctx, binding, new String[0]))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildBalanceBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .executes(ctx -> executeBrigadier(ctx, binding, new String[0]))
        .then(
            Commands.argument("spieler", StringArgumentType.word())
                .suggests((ctx, builder) -> suggestOnlinePlayers(builder))
                .executes(
                    ctx ->
                        executeBrigadier(ctx, binding, new String[] {getString(ctx, "spieler")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildEcoBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(buildEcoActionLiteral(binding, "add"))
        .then(buildEcoActionLiteral(binding, "set"))
        .then(buildEcoActionLiteral(binding, "take"))
        .build();
  }

  private com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack>
      buildEcoActionLiteral(CommandBinding binding, String action) {
    return Commands.literal(action)
        .then(
            Commands.argument("spieler", StringArgumentType.word())
                .suggests((ctx, builder) -> suggestOnlinePlayers(builder))
                .then(
                    Commands.argument("betrag", IntegerArgumentType.integer())
                        .executes(
                            ctx ->
                                executeBrigadier(
                                    ctx,
                                    binding,
                                    new String[] {
                                      action,
                                      getString(ctx, "spieler"),
                                      String.valueOf(getInteger(ctx, "betrag"))
                                    }))));
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildKristalleBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(
            Commands.argument("spieler", StringArgumentType.word())
                .suggests((ctx, builder) -> suggestOnlinePlayers(builder))
                .executes(
                    ctx ->
                        executeBrigadier(ctx, binding, new String[] {getString(ctx, "spieler")})))
        .then(
            Commands.literal("reset")
                .then(
                    Commands.argument("spieler", StringArgumentType.word())
                        .suggests((ctx, builder) -> suggestOnlinePlayers(builder))
                        .executes(
                            ctx ->
                                executeBrigadier(
                                    ctx,
                                    binding,
                                    new String[] {"reset", getString(ctx, "spieler")}))))
        .then(
            Commands.literal("give")
                .then(
                    Commands.argument("spieler", StringArgumentType.word())
                        .suggests((ctx, builder) -> suggestOnlinePlayers(builder))
                        .then(
                            Commands.argument("betrag", IntegerArgumentType.integer())
                                .executes(
                                    ctx ->
                                        executeBrigadier(
                                            ctx,
                                            binding,
                                            new String[] {
                                              "give",
                                              getString(ctx, "spieler"),
                                              String.valueOf(getInteger(ctx, "betrag"))
                                            })))))
        .then(
            Commands.literal("set")
                .then(
                    Commands.argument("spieler", StringArgumentType.word())
                        .suggests((ctx, builder) -> suggestOnlinePlayers(builder))
                        .then(
                            Commands.argument("betrag", IntegerArgumentType.integer())
                                .executes(
                                    ctx ->
                                        executeBrigadier(
                                            ctx,
                                            binding,
                                            new String[] {
                                              "set",
                                              getString(ctx, "spieler"),
                                              String.valueOf(getInteger(ctx, "betrag"))
                                            })))))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildCbBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .executes(ctx -> executeBrigadier(ctx, binding, new String[0]))
        .then(
            Commands.literal("reload")
                .executes(ctx -> executeBrigadier(ctx, binding, new String[] {"reload"})))
        .then(
            Commands.literal("info")
                .executes(ctx -> executeBrigadier(ctx, binding, new String[] {"info"})))
        .then(
            Commands.literal("debug")
                .executes(ctx -> executeBrigadier(ctx, binding, new String[] {"debug"}))
                .then(
                    Commands.literal("on")
                        .executes(
                            ctx -> executeBrigadier(ctx, binding, new String[] {"debug", "on"})))
                .then(
                    Commands.literal("off")
                        .executes(
                            ctx -> executeBrigadier(ctx, binding, new String[] {"debug", "off"})))
                .then(
                    Commands.literal("toggle")
                        .executes(
                            ctx ->
                                executeBrigadier(ctx, binding, new String[] {"debug", "toggle"}))))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildWerbungBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(
            Commands.argument("nachricht", StringArgumentType.greedyString())
                .executes(
                    ctx ->
                        executeBrigadier(ctx, binding, new String[] {getString(ctx, "nachricht")})))
        .build();
  }

  private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildWerbungTpBrigadier(
      CommandBinding binding) {
    return Commands.literal(binding.command().getName())
        .then(
            Commands.argument("uuid", StringArgumentType.word())
                .executes(
                    ctx -> executeBrigadier(ctx, binding, new String[] {getString(ctx, "uuid")})))
        .build();
  }

  private int executeBrigadier(
      CommandContext<CommandSourceStack> context, CommandBinding binding, String[] args) {
    CommandSender sender = context.getSource().getSender();
    binding.executor().onCommand(sender, binding.command(), binding.command().getName(), args);
    return Command.SINGLE_SUCCESS;
  }

  private java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions>
      suggestBrigadier(
          CommandContext<CommandSourceStack> context,
          com.mojang.brigadier.suggestion.SuggestionsBuilder builder,
          CommandBinding binding) {
    if (binding.tabCompleter() == null) {
      return builder.buildFuture();
    }

    String[] args = splitTabArgs(builder.getRemaining());
    List<String> suggestions =
        binding
            .tabCompleter()
            .onTabComplete(
                context.getSource().getSender(),
                binding.command(),
                binding.command().getName(),
                args);
    if (suggestions == null) {
      return builder.buildFuture();
    }

    for (String suggestion : suggestions) {
      if (suggestion != null && !suggestion.isEmpty()) {
        builder.suggest(suggestion);
      }
    }
    return builder.buildFuture();
  }

  private static String[] splitArgs(String input) {
    if (input == null || input.isBlank()) {
      return new String[0];
    }
    return input.trim().split("\\s+");
  }

  private static String[] splitTabArgs(String input) {
    if (input == null || input.isEmpty()) {
      return new String[] {""};
    }
    return input.split(" ", -1);
  }

  private java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions>
      suggestOnlinePlayers(com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
    String prefix = builder.getRemainingLowerCase();
    for (Player online : getServer().getOnlinePlayers()) {
      String name = online.getName();
      if (name.toLowerCase().startsWith(prefix)) {
        builder.suggest(name);
      }
    }
    return builder.buildFuture();
  }

  private java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions>
      suggestWarps(
          CommandContext<CommandSourceStack> context,
          com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
    String serverName = getConfig().getString("server-name", "citybuild-1");
    String prefix = builder.getRemainingLowerCase();
    for (String warpName : warpStore.listWarps(serverName)) {
      if (warpName.toLowerCase().startsWith(prefix)) {
        builder.suggest(warpName);
      }
    }
    return builder.buildFuture();
  }

  private java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions>
      suggestHomes(
          CommandContext<CommandSourceStack> context,
          com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
    if (!(context.getSource().getSender() instanceof Player player)) {
      return builder.buildFuture();
    }
    String prefix = builder.getRemainingLowerCase();
    for (String homeName : homes.listNames(player)) {
      if (homeName.toLowerCase().startsWith(prefix)) {
        builder.suggest(homeName);
      }
    }
    return builder.buildFuture();
  }

  private record CommandBinding(
      PluginCommand command, CommandExecutor executor, TabCompleter tabCompleter) {
    private CommandBinding {
      Objects.requireNonNull(command, "command");
      Objects.requireNonNull(executor, "executor");
    }
  }
}
