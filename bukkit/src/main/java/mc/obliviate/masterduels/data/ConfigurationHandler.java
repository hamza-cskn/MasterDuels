package mc.obliviate.masterduels.data;

import com.google.common.base.Preconditions;
import mc.obliviate.inventory.configurable.ConfigurableGuiCache;
import mc.obliviate.inventory.configurable.GuiConfigurationTable;
import mc.obliviate.inventory.configurable.util.ItemStackSerializer;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.arena.BasicArenaState;
import mc.obliviate.masterduels.arenaclear.modes.smart.SmartArenaClear;
import mc.obliviate.masterduels.bossbar.BossBarConfig;
import mc.obliviate.masterduels.bossbar.BossBarHandler;
import mc.obliviate.masterduels.game.MatchDataStorage;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.game.creator.MatchCreator;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.game.state.RoundStartingState;
import mc.obliviate.masterduels.gui.DuelArenaListGUI;
import mc.obliviate.masterduels.gui.creator.DuelMatchCreatorNonOwnerGUI;
import mc.obliviate.masterduels.gui.creator.DuelSettingsGUI;
import mc.obliviate.masterduels.gui.creator.DuelTeamManagerGUI;
import mc.obliviate.masterduels.gui.spectator.SpectatorTeleportationGUI;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.kit.gui.KitSelectionGUI;
import mc.obliviate.masterduels.kit.serializer.KitSerializer;
import mc.obliviate.masterduels.playerdata.history.gui.DuelHistoryLogGui;
import mc.obliviate.masterduels.queue.DuelQueueHandler;
import mc.obliviate.masterduels.queue.DuelQueueTemplate;
import mc.obliviate.masterduels.queue.gui.DuelQueueListGUI;
import mc.obliviate.masterduels.scoreboard.ScoreboardConfig;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.notify.NotifyActionStack;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import mc.obliviate.masterduels.utils.tab.TABManager;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import mc.obliviate.util.versiondetection.ServerVersionController;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class ConfigurationHandler {

    private static ConfigurationHandler instance = null;

    public static final String DATA_FILE_NAME = "arenas.yml";
    public static final String CONFIG_FILE_NAME = "config.yml";
    public static final String MESSAGES_FILE_NAME = "messages.yml";
    public static final String QUEUES_FILE_NAME = "queues.yml";
    public static final String MENUS_FILE_NAME = "menus.yml";
    public static final String KITS_FILE_NAME = "kits.yml";
    private static File dataFile;
    private final MasterDuels plugin;
    private static YamlConfiguration kits;
    private static YamlConfiguration data;
    private static YamlConfiguration config;
    //messages file instance in MessageUtils.class
    private static YamlConfiguration queues;
    private static YamlConfiguration menus;

    private static final Object[] objects = Utils.loadClass("mc.obliviate.masterduels.utils.initializer.MasterDuelsInitializer").getEnumConstants();

    private boolean prepared = false;

    private ConfigurationHandler(final MasterDuels plugin) {
        this.plugin = plugin;
    }

    public static ConfigurationHandler createInstance(final MasterDuels plugin) {
        if (instance == null) instance = new ConfigurationHandler(plugin);
        return instance;
    }

    public static ConfigurationSection getMenusSection(String sectionName) {
        return menus.getConfigurationSection(sectionName);
    }

    public static YamlConfiguration getQueues() {
        return queues;
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    public static YamlConfiguration getKits() {
        return kits;
    }

    public static YamlConfiguration getMenus() {
        return menus;
    }

    public void prepare() {
        loadDataFile(new File(plugin.getDataFolder().getPath() + File.separator + DATA_FILE_NAME));
        MessageUtils.setMessageConfig(loadResource(MESSAGES_FILE_NAME));
        config = loadResource(CONFIG_FILE_NAME);
        queues = loadResource(QUEUES_FILE_NAME);
        menus = loadResource(MENUS_FILE_NAME);
        kits = loadResource(KITS_FILE_NAME);
        this.prepared = true;
    }

    public void init() {
        Preconditions.checkState(prepared, "Configuration is not able to load without prepare.");

        ConfigurableGuiCache.resetCaches();
        GuiConfigurationTable.setDefaultConfigurationTable(new GuiConfigurationTable(menus));
        registerArenas();
        registerLobbyLocation();
        registerDelayEndDuelAfterPlayerKill();
        registerScoreboards();
        registerBossBars();
        registerTimerFormats();
        registerKits();
        registerGameCreatorLimits(config.getConfigurationSection("duel-creator.data-limits"));
        registerHistoryGui(menus.getConfigurationSection("game-history-gui"));
        registerKitSelectionGUIConfig(menus.getConfigurationSection("kit-selection-gui"));
        registerDuelListGUIConfig(menus.getConfigurationSection("duel-arenas-gui"));
        registerNotifyActions(config.getConfigurationSection("duel-game-lock.notify-actions"));
        registerTeamManagerConfig(menus.getConfigurationSection("duel-creator.manage-teams-gui"));
        registerDuelMatchCreatorNonOwnerGUIConfig(menus.getConfigurationSection("duel-creator.non-owner-gui"));
        registerGameRulesGui(menus.getConfigurationSection("duel-creator.game-rules-gui"));
        registerSpectatorTeleportationGui(menus.getConfigurationSection("spectator-teleportation-gui"));
        RoundStartingState.setLockDuration(Duration.ofSeconds(config.getInt("duel-game-lock.lock-duration", 7)));
        RoundStartingState.setLockFrequency(config.getInt("duel-game-lock.teleport-frequency"));
        TimerUtils.DATE_FORMAT = new SimpleDateFormat(MessageUtils.getMessageConfig().getString("time-format.date-format"));
        //MatchHistoryLog.GAME_HISTORY_LOG_ENABLED = config.getBoolean("game-history.enabled", true);
        Kit.USE_PLAYER_INVENTORIES = config.getBoolean("use-player-inventories", false);
        SmartArenaClear.REMOVE_ENTITIES = getConfig().getBoolean("arena-regeneration.remove-entities", true);

        if (ConfigurationHandler.getQueues().getBoolean("duel-queues-enabled", true))
            plugin.getDuelQueueHandler().init();
        if (ConfigurationHandler.getConfig().getBoolean("optimize-duel-worlds", false))
            plugin.getWorldOptimizerHandler().init();

    }

    private void registerKits() {
        for (final String key : kits.getKeys(false)) {
            KitSerializer.deserialize(kits.getConfigurationSection(key));
        }
    }

    private void registerNotifyActions(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            NotifyActionStack.deserialize(section.getConfigurationSection(key));
        }
    }

    public void initQueues() {
        DuelQueueHandler.enabled = true;
        registerQueues(queues.getConfigurationSection("queues"));
        registerDuelQueueGUIConfig(menus.getConfigurationSection("queues-gui"));
    }

    private YamlConfiguration loadResource(String fileName) {
        return loadResourceFile(new File(this.plugin.getDataFolder() + File.separator + fileName));
    }

    public YamlConfiguration loadResourceFile(File file) {
        YamlConfiguration section = YamlConfiguration.loadConfiguration(file);
        if (section.getKeys(false).isEmpty()) {
            this.plugin.saveResource(file.getName(), true);
            section = YamlConfiguration.loadConfiguration(file);
        }
        return section;
    }

    private void loadDataFile(File dataFile) {
        ConfigurationHandler.dataFile = dataFile;
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void registerGameCreatorLimits(ConfigurationSection section) {
        if (section == null) throw new IllegalArgumentException("section cannot null!");
        MatchCreator.MAX_GAME_TIME = section.getInt("max-game-time", 600);
        MatchCreator.MIN_GAME_TIME = section.getInt("min-game-time", 60);

        MatchCreator.MAX_TEAM_SIZE = section.getInt("max-team-size", 8);
        MatchCreator.MIN_TEAM_SIZE = section.getInt("min-team-size", 1);

        MatchCreator.MAX_TEAM_AMOUNT = section.getInt("max-team-amount", 10);
        MatchCreator.MIN_TEAM_AMOUNT = section.getInt("min-team-amount", 2);

        MatchCreator.MAX_ROUNDS = section.getInt("max-rounds", 5);
        MatchCreator.MIN_ROUNDS = section.getInt("min-rounds", 1);

        final List<String> gameRules = section.getStringList("allowed-game-rules");
        boolean allOfThem = gameRules.contains("*");
        MatchCreator.ALLOWED_GAME_RULES.clear();
        for (GameRule rule : GameRule.values()) {
            if (allOfThem || gameRules.contains(rule.name())) {
                MatchCreator.ALLOWED_GAME_RULES.add(rule);
            }
        }

        final List<String> gameKits = section.getStringList("allowed-kits");
        allOfThem = gameKits.contains("*");
        MatchCreator.ALLOWED_KITS.clear();
        for (Kit kit : Kit.getKits().values()) {
            if (allOfThem || gameKits.contains(kit.getKitName())) {
                MatchCreator.ALLOWED_KITS.add(kit);
            }
        }

    }

    private void registerQueues(final ConfigurationSection section) {
        if (section != null && !section.getKeys(false).isEmpty()) {
            for (final String key : section.getKeys(false)) {
                DuelQueueTemplate.deserialize(section.getConfigurationSection(key));
            }
        }
    }

    private void registerDuelQueueGUIConfig(final ConfigurationSection section) {
        final Map<String, ItemStack> iconItemStacks = new HashMap<>();

        //firstly, deserialize default icon.
        final ItemStack defaultIcon = ItemStackSerializer.deserializeItemStack(section.getConfigurationSection("icons.queue-icons.default"), GuiConfigurationTable.getDefaultConfigurationTable());
        iconItemStacks.put("default", defaultIcon);

        final ConfigurationSection iconsSection = section.getConfigurationSection("icons.queue-icons");

        for (final DuelQueueTemplate template : DuelQueueTemplate.getQueueTemplates()) {
            final String key = template.getName();
            if (key.equalsIgnoreCase("default")) continue;

            if (!iconsSection.isSet(key)) {
                iconItemStacks.put(key, defaultIcon);
                continue;
            }

            final ItemStack item = ItemStackSerializer.deserializeItemStack(iconsSection.getConfigurationSection(key), GuiConfigurationTable.getDefaultConfigurationTable());


            if (item.getItemMeta() != null) {
                if (item.getItemMeta().getLore() == null) {
                    final ItemMeta meta = item.getItemMeta();
                    meta.setLore(defaultIcon.getItemMeta().getLore());
                    item.setItemMeta(meta);
                }
                if (item.getItemMeta().getDisplayName() == null) {
                    final ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(defaultIcon.getItemMeta().getDisplayName());
                    item.setItemMeta(meta);
                }
            } else {
                Logger.error("Queue icon could not deserialized normally. (" + key + ")");
            }

            iconItemStacks.put(key, item);

        }

        final int zeroAmount = section.getBoolean("use-zero-amount", false) ? 0 : 1;

        new DuelQueueListGUI.Config(zeroAmount, section.getInt("size", 6), section.getString("title", "Queues"), iconItemStacks, section.getConfigurationSection("icons"));

    }

    private List<Integer> parseStringAsIntegerList(String str) {
        final List<Integer> pageSlots = new ArrayList<>();
        final String[] slotStrings = str.split(",");

        for (final String slotText : slotStrings) {
            try {
                pageSlots.add(Integer.parseInt(slotText));
            } catch (NumberFormatException ignore) {
            }
        }
        return pageSlots;
    }

    private void registerKitSelectionGUIConfig(final ConfigurationSection section) {
        new KitSelectionGUI.Config(parseStringAsIntegerList(section.getString("page-slots")));
    }

    private List<ItemStack> parseItemStackList(final List<String> list) {
        final List<ItemStack> teamIcons = new ArrayList<>();
        for (String materialName : list) {
            XMaterial.matchXMaterial(materialName).ifPresent(mat -> teamIcons.add(mat.parseItem()));
        }
        return teamIcons;
    }

    private void registerDuelMatchCreatorNonOwnerGUIConfig(final ConfigurationSection section) {
        new DuelMatchCreatorNonOwnerGUI.Config(
                ItemStackSerializer.deserializeItemStack(section.getConfigurationSection("icons.empty-player-slot"), GuiConfigurationTable.getDefaultConfigurationTable()),
                ItemStackSerializer.deserializeItemStack(section.getConfigurationSection("icons.player-slot"), GuiConfigurationTable.getDefaultConfigurationTable()),
                section.getString("icons.team-slot.display-name"),
                section.getStringList("icons.team-slot.lore"), parseItemStackList(menus.getStringList("duel-creator.manage-teams-gui.icons.team-slot.dynamic-materials")));
    }

    private void registerTeamManagerConfig(final ConfigurationSection section) {
        new DuelTeamManagerGUI.Config(
                ItemStackSerializer.deserializeItemStack(section.getConfigurationSection("icons.empty-player-slot"), GuiConfigurationTable.getDefaultConfigurationTable()),
                ItemStackSerializer.deserializeItemStack(section.getConfigurationSection("icons.player-slot"), GuiConfigurationTable.getDefaultConfigurationTable()),
                section.getString("icons.team-slot.display-name"),
                section.getStringList("icons.team-slot.lore"), parseItemStackList(menus.getStringList("duel-creator.non-owner-gui.icons.team-slot.dynamic-materials")));
    }

    private void registerDuelListGUIConfig(final ConfigurationSection section) {
        final Map<BasicArenaState, ItemStack> icons = new HashMap<>();
        for (final BasicArenaState state : BasicArenaState.values()) {
            icons.put(state, ItemStackSerializer.deserializeItemStack(section.getConfigurationSection("icons." + state.name()), GuiConfigurationTable.getDefaultConfigurationTable()));
        }

        final List<Integer> pageSlots = parseStringAsIntegerList(section.getString("page-slots"));

        new DuelArenaListGUI.Config(icons, pageSlots);
    }

    private void registerHistoryGui(final ConfigurationSection section) {
        new DuelHistoryLogGui.Config(parseStringAsIntegerList(section.getString("page-slots")));
    }

    private void registerSpectatorTeleportationGui(final ConfigurationSection section) {
        new SpectatorTeleportationGUI.Config(parseStringAsIntegerList(section.getString("page-slots")));
    }

    private void registerGameRulesGui(final ConfigurationSection section) {
        new DuelSettingsGUI.Config(section);
    }

    private void registerArenas() {
        Arena.unregisterAllArenas();
        for (final String arenaName : data.getKeys(false)) {
            Arena.deserialize(data.getConfigurationSection(arenaName));
        }
    }

    private void registerLobbyLocation() {
        if (data.isSet("lobby-location")) {
            DataHandler.setLobbyLocation(SerializerUtils.deserializeLocationYAML(data.getConfigurationSection("lobby-location")));
        }
    }

    private void registerDelayEndDuelAfterPlayerKill() {
        MatchDataStorage.setEndDelay(Duration.ofSeconds(config.getInt("delay-end-duel-after-player-kill", 20)));
    }

    private void registerScoreboards() {
        ScoreboardConfig scoreboardConfig = new ScoreboardConfig(config.getInt("scoreboards.update-interval", 20));
        for (final MatchStateType gameState : MatchStateType.values()) {
            final ConfigurationSection section = config.getConfigurationSection("scoreboards." + gameState.name());
            if (section == null) {
                scoreboardConfig.registerFormatConfig(gameState, "{name} &c{health}HP", "{name} &cDEAD", "{name} &cQUIT", "&e&lDuels", Arrays.asList("{+opponents}", "", "&4&lERROR: &c" + gameState, "&7No Lines Found"));
                continue;
            }
            scoreboardConfig.registerFormatConfig(gameState, section.getString("live-opponent-format"), section.getString("dead-opponent-format"), section.getString("quit-opponent-format"), section.getString("title"), section.getStringList("lines"));
        }
        ScoreboardConfig.setDefaultConfig(scoreboardConfig);
    }

    private void registerBossBars() {
        if (config.getBoolean("boss-bars.enabled", false)) {
            BossBarHandler.BossBarModule module = BossBarHandler.BossBarModule.DISABLED;
            switch (config.getString("boss-bars.mode")) {
                case "INTERNAL":
                    if (ServerVersionController.isServerVersionAbove(ServerVersionController.V1_8)) {
                        module = BossBarHandler.BossBarModule.INTERNAL;
                    }
                case "TAB":
                    if (TABManager.isEnabled()) {
                        module = BossBarHandler.BossBarModule.TAB;
                    }
                default:
                    if (ServerVersionController.isServerVersionAbove(ServerVersionController.V1_8)) {
                        module = BossBarHandler.BossBarModule.INTERNAL;
                    } else if (TABManager.isEnabled()) {
                        module = BossBarHandler.BossBarModule.TAB;
                    }
            }

            BossBarHandler.setBossBarModule(module);
        } else {
            BossBarHandler.setBossBarModule(BossBarHandler.BossBarModule.DISABLED);
        }
        BossBarHandler.setDefaultConfig(new BossBarConfig(config.getString("boss-bars.in-battle"), config.getString("boss-bars.arena-closing")));
    }

    private void registerTimerFormats() {
        TimerUtils.MINUTES = MessageUtils.getMessageConfig().getString("time-format.minutes");
        TimerUtils.MINUTE = MessageUtils.getMessageConfig().getString("time-format.minute");
        TimerUtils.SECONDS = MessageUtils.getMessageConfig().getString("time-format.seconds");
        TimerUtils.SECOND = MessageUtils.getMessageConfig().getString("time-format.second");
    }

    public YamlConfiguration getData() {
        return data;
    }

    public void saveDataFile() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveArena(final Arena arena) {
        final ConfigurationSection section = data.createSection(arena.getName());
        arena.serialize(section);
        saveDataFile();
    }

    public void deleteArena(final Arena arena) {
        data.set(arena.getName(), null);
        saveDataFile();
    }

}
