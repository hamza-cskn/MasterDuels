package mc.obliviate.blokduels.config;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigHandler {

	private final BlokDuels plugin;
	private static final String MESSAGES_FILE_NAME = "messages.yml";

	public ConfigHandler(BlokDuels plugin) {
		this.plugin = plugin;
	}

	public void init() {
		final File file = new File(plugin.getDataFolder() + File.separator + MESSAGES_FILE_NAME);
		Bukkit.broadcastMessage(plugin.getDataFolder() + File.separator + MESSAGES_FILE_NAME);
		YamlConfiguration messages = YamlConfiguration.loadConfiguration(file);

		if (messages.getKeys(false).isEmpty()) {
			plugin.saveResource(MESSAGES_FILE_NAME,true);
			messages = YamlConfiguration.loadConfiguration(file);
		}

		MessageUtils.setMessageConfig(messages);

	}


}
