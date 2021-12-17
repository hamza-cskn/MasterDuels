package mc.obliviate.blokduels.data;

import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameState;
import mc.obliviate.blokduels.game.bossbar.BossBarData;
import mc.obliviate.blokduels.utils.Logger;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.scoreboard.ScoreboardFormatConfig;
import mc.obliviate.blokduels.utils.scoreboard.ScoreboardManager;
import mc.obliviate.blokduels.utils.serializer.SerializerUtils;
import mc.obliviate.blokduels.utils.timer.TimerUtils;
import mc.obliviate.blokduels.utils.title.TitleHandler;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class YamlStorageHandler {

	private static final String DATA_FILE_NAME = "arenadatas.yml";
	private static final String CONFIG_FILE_NAME = "config.yml";
	private static final String MESSAGES_FILE_NAME = "messages.yml";
	private static File dataFile;
	private final BlokDuels plugin;
	private YamlConfiguration data;
	private YamlConfiguration config;

	public YamlStorageHandler(final BlokDuels plugin) {
		this.plugin = plugin;
	}

	public void init() {
		loadDataFile(new File(plugin.getDataFolder() + File.separator + DATA_FILE_NAME));
		loadMessagesFile(new File(plugin.getDataFolder() + File.separator + MESSAGES_FILE_NAME));
		loadConfigFile(new File(plugin.getDataFolder() + File.separator + CONFIG_FILE_NAME));

		registerArenas();
		optimizeWorlds();
		registerLobbyLocation();
		registerDelayEndDuelAfterPlayerKill();
		registerScoreboards();
		registerTitles();
		registerBossbars();
		registerTimerFormats();
	}

	private void loadDataFile(File dataFile) {
		YamlStorageHandler.dataFile = dataFile;
		data = YamlConfiguration.loadConfiguration(dataFile);
	}

	private void loadMessagesFile(File messagesFile) {
		YamlConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);

		if (messages.getKeys(false).isEmpty()) {
			plugin.saveResource(MESSAGES_FILE_NAME,true);
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

	private void registerArenas() {
		for (final String arenaName : data.getKeys(false)) {
			final Arena arena = Arena.deserialize(data.getConfigurationSection(arenaName));
			DataHandler.registerArena(arena);
		}
	}

	private void optimizeWorlds() {
		if (config.getBoolean("optimize-duel-worlds", false)) {
			for (final Arena arena : DataHandler.getArenas().keySet()) {
				final World world = arena.getArenaCuboid().getPoint1().getWorld();
				world.setMonsterSpawnLimit(0);
				world.setAnimalSpawnLimit(0);
				world.setAmbientSpawnLimit(0);
				world.setWaterAnimalSpawnLimit(0);
				world.setKeepSpawnInMemory(false);
				world.setThundering(false);
				world.setStorm(false);
				if (!world.getPVP()) {
					Logger.warn("PVP is disabled at " + world.getName() + " world! Then why i am here :(");
				}
			}
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
			BossBarData.NORMAL_TEXT_FORMAT = config.getString("bossbars.in-battle");
			BossBarData.CLOSING_TEXT_FORMAT = config.getString("bossbars.arena-closing");
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
		TimerUtils.MINUTES = MessageUtils.getMessageConfig().getString("timer-format.minutes");
		TimerUtils.MINUTE = MessageUtils.getMessageConfig().getString("timer-format.minute");
		TimerUtils.SECONDS = MessageUtils.getMessageConfig().getString("timer-format.seconds");
		TimerUtils.SECOND = MessageUtils.getMessageConfig().getString("timer-format.second");

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
