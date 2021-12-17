package mc.obliviate.blokduels;

import com.hakan.messageapi.bukkit.MessageAPI;
import mc.obliviate.blokduels.commands.DuelArenasCMD;
import mc.obliviate.blokduels.commands.DuelCMD;
import mc.obliviate.blokduels.commands.KitEditorCMD;
import mc.obliviate.blokduels.commands.SetupCMD;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.data.SQLManager;
import mc.obliviate.blokduels.data.YamlStorageHandler;
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

	private static boolean shutdownMode = false;
	private final SQLManager sqlManager = new SQLManager(this);
	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private final YamlStorageHandler yamlStorageHandler = new YamlStorageHandler(this);
	private MessageAPI messageAPI;
	private ScoreboardManager scoreboardManager;

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
		yamlStorageHandler.init();
		inventoryAPI.init();
		scoreboardManager = new ScoreboardManager(this);
		messageAPI = MessageAPI.getInstance(this);
		new TABManager(this);
		sqlManager.init();
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
		Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
	}

	private void loadKits() {
		final File file = new File(getDataFolder().getPath() + File.separator + "kits.yml");
		final YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
		for (final String key : data.getKeys(false)) {
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

	public YamlStorageHandler getDatabaseHandler() {
		return yamlStorageHandler;
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

	public SQLManager getSqlManager() {
		return sqlManager;
	}
}
