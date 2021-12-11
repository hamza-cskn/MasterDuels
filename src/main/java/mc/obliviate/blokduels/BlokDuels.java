package mc.obliviate.blokduels;

import com.hakan.messageapi.bukkit.MessageAPI;
import mc.obliviate.blokduels.commands.DuelArenasCMD;
import mc.obliviate.blokduels.commands.DuelCMD;
import mc.obliviate.blokduels.commands.KitEditorCMD;
import mc.obliviate.blokduels.commands.SetupCMD;
import mc.obliviate.blokduels.config.ConfigHandler;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.data.DatabaseHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.kit.serializer.KitSerializer;
import mc.obliviate.blokduels.listeners.*;
import mc.obliviate.blokduels.utils.scoreboard.ScoreboardManager;
import mc.obliviate.blokduels.utils.tab.TABManager;
import mc.obliviate.inventory.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BlokDuels extends JavaPlugin {

	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private final DatabaseHandler databaseHandler = new DatabaseHandler(this);
	private final ConfigHandler configHandler = new ConfigHandler(this);
	private MessageAPI messageAPI;
	private ScoreboardManager scoreboardManager;
	private static boolean shutdownMode = false;

	public static boolean isInShutdownMode() {
		return shutdownMode;
	}

	@Override
	public void onEnable() {
		registerListeners();
		registerCommands();
		setupHandlers();
		loadKits();
		shutdownMode = false;
	}

	private void setupHandlers() {
		databaseHandler.init();
		inventoryAPI.init();
		configHandler.init();
		scoreboardManager = new ScoreboardManager(this);
		messageAPI = MessageAPI.getInstance(this);
		new TABManager(this);
	}

	private void registerCommands() {
		getCommand("duel").setExecutor(new DuelCMD(this));
		getCommand("duelarenas").setExecutor(new DuelArenasCMD(this));
		getCommand("duelsetup").setExecutor(new SetupCMD(this));
		getCommand("duelkitsave").setExecutor(new KitEditorCMD(this));
	}

	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new DuelProtectListener(this), this);
		Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
		Bukkit.getPluginManager().registerEvents(new TeleportListener(), this);
	}

	private void loadKits() {
		final File file = new File(getDataFolder().getPath() + File.separator + "kits.yml");
		final YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
		for (String key : data.getKeys(false)) {
			KitSerializer.deserialize(data.getConfigurationSection(key));
		}


	}

	@Override
	public void onDisable() {
		shutdownMode = true;
		for (final Game game : DataHandler.getArenas().values()) {
			if (game != null) {
				game.uninstallGame();
			}
		}
	}

	public ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public DatabaseHandler getDatabaseHandler() {
		return databaseHandler;
	}

	public InventoryAPI getInventoryAPI() {
		return inventoryAPI;
	}

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public MessageAPI getMessageAPI() {
		return messageAPI;
	}
}
