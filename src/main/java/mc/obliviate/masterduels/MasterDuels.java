package mc.obliviate.masterduels;

import com.hakan.messageapi.bukkit.MessageAPI;
import mc.obliviate.masterduels.arenaclear.ArenaClearHandler;
import mc.obliviate.masterduels.commands.DuelCMD;
import mc.obliviate.masterduels.commands.DuelAdminCMD;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.data.YamlStorageHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.gamerule.GameRuleListener;
import mc.obliviate.masterduels.kit.serializer.KitSerializer;
import mc.obliviate.masterduels.listeners.*;
import mc.obliviate.masterduels.utils.scoreboard.ScoreboardManager;
import mc.obliviate.masterduels.utils.tab.TABManager;
import mc.obliviate.masterduels.utils.timer.SQLCacheTimer;
import mc.obliviate.inventory.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MasterDuels extends JavaPlugin {

	private static boolean shutdownMode = false;
	private final SQLManager sqlManager = new SQLManager(this);
	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private final ArenaClearHandler arenaClearHandler = new ArenaClearHandler(this);
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
		setupTimers();
		loadKits();
		shutdownMode = false;
	}

	private void setupHandlers() {
		yamlStorageHandler.init();
		inventoryAPI.init();
		if (yamlStorageHandler.getConfig().getBoolean("arena-regeneration.enabled")) {
			arenaClearHandler.init();
		}
		scoreboardManager = new ScoreboardManager(this);
		messageAPI = MessageAPI.getInstance(this);
		new TABManager(this);
		sqlManager.init();
	}

	private void setupTimers() {
		new SQLCacheTimer().init(this);
	}

	private void registerCommands() {
		getCommand("duel").setExecutor(new DuelCMD(this));
		getCommand("dueladmin").setExecutor(new DuelAdminCMD(this));
	}

	private void registerListeners() {
		//RollbackListener registering from ArenaClear.java
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new DuelProtectListener(this), this);
		Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
		Bukkit.getPluginManager().registerEvents(new TeleportListener(), this);
		Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
		Bukkit.getPluginManager().registerEvents(new GameRuleListener(), this);
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
		getSqlManager().disconnect();
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

	public ArenaClearHandler getArenaClearHandler() {
		return arenaClearHandler;
	}
}