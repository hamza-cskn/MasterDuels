package mc.obliviate.blokduels.data;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameState;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.scoreboard.ScoreboardFormatConfig;
import mc.obliviate.blokduels.utils.scoreboard.ScoreboardManager;
import mc.obliviate.blokduels.utils.serializer.SerializerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DatabaseHandler {

	private static final String DATA_FILE_NAME = "data.yml";
	private static final String CONFIG_FILE_NAME = "config.yml";
	private static File dataFile;
	private final BlokDuels plugin;
	private YamlConfiguration data;
	private YamlConfiguration config;

	public DatabaseHandler(final BlokDuels plugin) {
		this.plugin = plugin;
	}

	public void init() {

		dataFile = new File(plugin.getDataFolder() + File.separator + DATA_FILE_NAME);
		final File configFile = new File(plugin.getDataFolder() + File.separator + CONFIG_FILE_NAME);

		data = YamlConfiguration.loadConfiguration(dataFile);
		config = YamlConfiguration.loadConfiguration(configFile);
		if (config.getKeys(false).isEmpty()) {
			plugin.saveResource(CONFIG_FILE_NAME, true);
		}

		for (String arenaName : data.getKeys(false)) {
			final Arena arena = Arena.deserialize(data.getConfigurationSection(arenaName));
			DataHandler.registerArena(arena);
		}

		if (data.isSet("lobby-location")) {
			DataHandler.setLobbyLocation(SerializerUtils.deserializeLocation(data.getConfigurationSection("lobby-location")));
		}

		Game.setEndDelay(getConfig().getInt("delay-end-duel-after-player-kill", 20));

		for (GameState gameState : GameState.values()) {
			final ConfigurationSection section = getConfig().getConfigurationSection("scoreboards." + gameState);
			ScoreboardFormatConfig scoreboardFormatConfig;
			if (section == null) {
				scoreboardFormatConfig = new ScoreboardFormatConfig(MessageUtils.parseColor("{name} &c{health}HP"), MessageUtils.parseColor("{name} &cDEAD"), MessageUtils.parseColor("&e&lDuels"), MessageUtils.parseColor(Arrays.asList("{+opponents}", "", "&4&lERROR: &8" + gameState, "&cNo Lines Found")));
			} else {
				scoreboardFormatConfig = new ScoreboardFormatConfig(MessageUtils.parseColor(section.getString("live-opponent-format")), MessageUtils.parseColor(section.getString("dead-opponent-format")), MessageUtils.parseColor(section.getString("title")), MessageUtils.parseColor(section.getStringList("lines")));
			}
			ScoreboardManager.getScoreboardLines().put(gameState, scoreboardFormatConfig);

		}
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
