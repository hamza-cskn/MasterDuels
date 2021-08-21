package mc.obliviate.blokduels.data;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DatabaseHandler {

	private final BlokDuels plugin;
	private static YamlConfiguration data;
	private static YamlConfiguration config;
	private static File dataFile;
	private static File configFile;

	public DatabaseHandler(final BlokDuels plugin) {
		this.plugin = plugin;
	}

	public void init() {

		dataFile = new File(plugin.getDataFolder() + File.separator + "data.yml");
		configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");

		data = YamlConfiguration.loadConfiguration(dataFile);
		config = YamlConfiguration.loadConfiguration(configFile);
		if (config.getKeys(false).isEmpty()) {
			plugin.saveResource("config.yml", true);
		}

		for (String arenaName : data.getKeys(false)) {
			final Arena arena = Arena.deserialize(data.getConfigurationSection(arenaName));
			DataHandler.registerArena(arena);
		}
	}

	public static YamlConfiguration getData() {
		return data;
	}

	public static YamlConfiguration getConfig() {
		return config;
	}

	public static void saveArena(final Arena arena) {
		final ConfigurationSection section = data.createSection(arena.getName());

		arena.serialize(section);

		try {
			data.save(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
