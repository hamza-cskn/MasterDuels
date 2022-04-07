package mc.obliviate.masterduels.data;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.arena.BasicArenaState;
import mc.obliviate.masterduels.arenaclear.modes.smart.SmartArenaClear;
import mc.obliviate.masterduels.bossbar.TABBossbarManager;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameCreator;
import mc.obliviate.masterduels.game.GameState;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.gui.DuelArenaListGUI;
import mc.obliviate.masterduels.gui.DuelHistoryLogGUI;
import mc.obliviate.masterduels.history.GameHistoryLog;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.queue.DuelQueueHandler;
import mc.obliviate.masterduels.queue.DuelQueueTemplate;
import mc.obliviate.masterduels.queue.gui.DuelQueueListGUI;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.scoreboard.ScoreboardFormatConfig;
import mc.obliviate.masterduels.utils.scoreboard.ScoreboardManager;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import mc.obliviate.masterduels.utils.title.TitleHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class YamlStorageHandler {

	private static final String DATA_FILE_NAME = "arenas.yml";
	private static final String CONFIG_FILE_NAME = "config.yml";
	private static final String MESSAGES_FILE_NAME = "messages.yml";
	private static final String QUEUES_FILE_NAME = "queues.yml";
	private static File dataFile;
	private final MasterDuels plugin;
	private YamlConfiguration data;
	private YamlConfiguration queues;
	private YamlConfiguration config;

	public YamlStorageHandler(final MasterDuels plugin) {
		this.plugin = plugin;
	}

	public void init() {
		loadDataFile(new File(plugin.getDataFolder() + File.separator + DATA_FILE_NAME));
		loadMessagesFile(new File(plugin.getDataFolder() + File.separator + MESSAGES_FILE_NAME));
		loadConfigFile(new File(plugin.getDataFolder() + File.separator + CONFIG_FILE_NAME));


		registerArenas();
		registerLobbyLocation();
		registerDelayEndDuelAfterPlayerKill();
		registerScoreboards();
		registerTitles();
		registerBossbars();
		registerTimerFormats();
		registerGameCreatorLimits(config.getConfigurationSection("duel-creator.data-limits"));
		registerHistoryGui();
		registerDuelListGUIConfig(config.getConfigurationSection("duel-arenas-gui"));

		initQueues();

		TimerUtils.DATE_FORMAT = new SimpleDateFormat(MessageUtils.getMessageConfig().getString("time-format.date-format"));
		GameHistoryLog.GAME_HISTORY_LOG_ENABLED = config.getBoolean("game-history.enabled", true);
		DataHandler.LOCK_TIME_IN_SECONDS = config.getInt("game-starting-lock-time", 3);
		Kit.USE_PLAYER_INVENTORIES = config.getBoolean("use-player-inventories", false);
		SmartArenaClear.REMOVE_ENTITIES = plugin.getDatabaseHandler().getConfig().getBoolean("arena-regeneration.remove-entities", true);

	}


	private void initQueues() {
		DuelQueueHandler.enabled = true;
		loadQueuesFile(new File(plugin.getDataFolder() + File.separator + QUEUES_FILE_NAME));
		registerQueues(queues.getConfigurationSection("queues"));
		registerDuelQueueGUIConfig(queues.getConfigurationSection("queues-gui"));
	}

	private void loadDataFile(File dataFile) {
		YamlStorageHandler.dataFile = dataFile;
		data = YamlConfiguration.loadConfiguration(dataFile);
	}

	private void loadMessagesFile(File messagesFile) {
		YamlConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);

		if (messages.getKeys(false).isEmpty()) {
			plugin.saveResource(MESSAGES_FILE_NAME, true);
			messages = YamlConfiguration.loadConfiguration(messagesFile);
		}

		MessageUtils.setMessageConfig(messages);
	}


	private void loadConfigFile(File configFile) {
		config = YamlConfiguration.loadConfiguration(configFile);
		if (config.getKeys(false).isEmpty()) {
			plugin.saveResource(CONFIG_FILE_NAME, true);
			config = YamlConfiguration.loadConfiguration(configFile);
		}
	}

	private void loadQueuesFile(File queueFile) {
		queues = YamlConfiguration.loadConfiguration(queueFile);
		if (queues.getKeys(false).isEmpty()) {
			plugin.saveResource(QUEUES_FILE_NAME, true);
			queues = YamlConfiguration.loadConfiguration(queueFile);
		}
	}

	private void registerGameCreatorLimits(ConfigurationSection section) {
		if (section == null) throw new IllegalArgumentException("section cannot null!");
		GameCreator.MAX_GAME_TIME = section.getInt("max-game-time", 600);
		GameCreator.MIN_GAME_TIME = section.getInt("min-game-time", 60);

		GameCreator.MAX_TEAM_SIZE = section.getInt("max-team-size", 8);
		GameCreator.MIN_TEAM_SIZE = section.getInt("min-team-size", 1);

		GameCreator.MAX_TEAM_AMOUNT = section.getInt("max-team-amount", 10);
		GameCreator.MIN_TEAM_AMOUNT = section.getInt("min-team-amount", 2);

		GameCreator.MAX_ROUNDS = section.getInt("max-rounds", 5);
		GameCreator.MIN_ROUNDS = section.getInt("min-rounds", 1);

		final List<String> gameRules = section.getStringList("allowed-game-rules");
		boolean allOfThem = gameRules.contains("*");
		for (GameRule rule : GameRule.values()) {
			if (allOfThem || gameRules.contains(rule.name())) {
				GameCreator.ALLOWED_GAME_RULES.add(rule);
			}
		}

		final List<String> gameKits = section.getStringList("allowed-kits");
		allOfThem = gameKits.contains("*");
		for (Kit kit : Kit.getKits().values()) {
			if (allOfThem || gameKits.contains(kit.getKitName())) {
				GameCreator.ALLOWED_KITS.add(kit);
			}
		}

		Bukkit.broadcastMessage(section.getStringList("allowed-kits").toString());
		Bukkit.broadcastMessage(allOfThem + "");
		Bukkit.broadcastMessage(GameCreator.ALLOWED_KITS.toString());
	}

	private void registerQueues(final ConfigurationSection section) {
		if (section != null && !section.getKeys(false).isEmpty()) {
			for (final String key : section.getKeys(false)) {
				DuelQueueTemplate.deserialize(plugin, section.getConfigurationSection(key));
			}
		}
	}

	private void registerDuelQueueGUIConfig(final ConfigurationSection section) {
		final Map<String, ItemStack> iconItemStacks = new HashMap<>();

		//firstly, deserialize default icon.
		final ItemStack defaultIcon = SerializerUtils.deserializeItemStack(section.getConfigurationSection("icons.functional-icons.queue-icons.default"), null);
		iconItemStacks.put("default", defaultIcon);

		final ConfigurationSection iconsSection = section.getConfigurationSection("icons.functional-icons.queue-icons");

		for (final DuelQueueTemplate template : DuelQueueTemplate.getQueueTemplates()) {
			final String key = template.getName();
			if (key.equalsIgnoreCase("default")) continue;

			if (!iconsSection.isSet(key)) {
				iconItemStacks.put(key, defaultIcon);
				continue;
			}

			final ItemStack item = SerializerUtils.deserializeItemStack(iconsSection.getConfigurationSection(key), null);


			if (item == null) {
				iconItemStacks.put(key, defaultIcon);

			} else {
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
		}

		final int zeroAmount = section.getBoolean("use-zero-amount", false) ? 0 : 1;

		DuelQueueListGUI.guiConfig = new DuelQueueListGUI.DuelQueueListGUIConfig(zeroAmount, section.getInt("size", 6), section.getString("title", "Queues"), iconItemStacks, section.getConfigurationSection("icons"));

	}

	private void registerDuelListGUIConfig(final ConfigurationSection section) {
		final Map<BasicArenaState, ItemStack> icons = new HashMap<>();
		for (final BasicArenaState state : BasicArenaState.values()) {
			icons.put(state, SerializerUtils.deserializeItemStack(section.getConfigurationSection("icons." + state.name()), null));
		}

		DuelArenaListGUI.guiConfig = new DuelArenaListGUI.DuelArenaListGUIConfig(icons, section.getString("title", "Duel Arenas"));
	}

	private void registerHistoryGui() {
		DuelHistoryLogGUI.guiSection = config.getConfigurationSection("game-history.gui");
		DuelHistoryLogGUI.guiConfig = new DuelHistoryLogGUI.DuelHistoryLogGUIConfig(DuelHistoryLogGUI.guiSection);
	}

	private void registerArenas() {
		for (final String arenaName : data.getKeys(false)) {
			final Arena arena = Arena.deserialize(data.getConfigurationSection(arenaName));
			DataHandler.registerArena(arena);
		}
	}

	private void registerLobbyLocation() {
		if (data.isSet("lobby-location")) {
			DataHandler.setLobbyLocation(SerializerUtils.deserializeLocationYAML(data.getConfigurationSection("lobby-location")));
		}
	}

	private void registerDelayEndDuelAfterPlayerKill() {
		Game.setEndDelay(getConfig().getInt("delay-end-duel-after-player-kill", 20));
	}

	private void registerScoreboards() {
		for (final GameState gameState : GameState.values()) {
			final ConfigurationSection section = config.getConfigurationSection("scoreboards." + gameState.name());
			ScoreboardFormatConfig scoreboardFormatConfig;
			if (section == null) {
				scoreboardFormatConfig = new ScoreboardFormatConfig(MessageUtils.parseColor("{name} &c{health}HP"), MessageUtils.parseColor("{name} &cDEAD"), MessageUtils.parseColor("&e&lDuels"), MessageUtils.parseColor(Arrays.asList("{+opponents}", "", "&4&lERROR: &8" + gameState, "&cNo Lines Found")));
			} else {
				scoreboardFormatConfig = new ScoreboardFormatConfig(MessageUtils.parseColor(section.getString("live-opponent-format")), MessageUtils.parseColor(section.getString("dead-opponent-format")), MessageUtils.parseColor(section.getString("title")), MessageUtils.parseColor(section.getStringList("lines")));
			}
			ScoreboardManager.getScoreboardLines().put(gameState, scoreboardFormatConfig);
		}
	}

	private void registerBossbars() {
		if (config.getBoolean("bossbars.enabled", false)) {
			TABBossbarManager.NORMAL_TEXT_FORMAT = config.getString("bossbars.in-battle");
			TABBossbarManager.CLOSING_TEXT_FORMAT = config.getString("bossbars.arena-closing");
		}
	}

	private void registerTitles() {
		for (final TitleHandler.TitleType type : TitleHandler.TitleType.values()) {
			final ConfigurationSection section = config.getConfigurationSection("titles." + type.name());
			if (section == null) return;
			TitleHandler.registerTitle(type, section);
		}
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

	public YamlConfiguration getConfig() {
		return config;
	}

	public void saveArena(final Arena arena) {
		final ConfigurationSection section = data.createSection(arena.getName());
		arena.serialize(section);
		saveDataFile();

	}

}
