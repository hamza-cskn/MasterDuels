package mc.obliviate.blokduels.data;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.utils.serializer.SerializerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DatabaseHandler {

	private final BlokDuels plugin;
	private static final String DATA_FILE_NAME = "data.yml";
	private static final String CONFIG_FILE_NAME = "config.yml";
	private YamlConfiguration data;
	private YamlConfiguration config;
	private static File dataFile;

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
