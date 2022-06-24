package mc.obliviate.masterduels;

import com.hakan.core.HCore;
import mc.obliviate.inventory.InventoryAPI;
import mc.obliviate.masterduels.arenaclear.ArenaClearListener;
import mc.obliviate.masterduels.arenaclear.IArenaClearHandler;
import mc.obliviate.masterduels.arenaclear.modes.smart.SmartArenaClearHandler;
import mc.obliviate.masterduels.commands.DeveloperCMD;
import mc.obliviate.masterduels.commands.DuelAdminCMD;
import mc.obliviate.masterduels.commands.DuelCMD;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.data.YamlStorageHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.gamerule.MatchRuleListener;
import mc.obliviate.masterduels.history.MatchHistoryLog;
import mc.obliviate.masterduels.kit.serializer.KitSerializer;
import mc.obliviate.masterduels.listeners.*;
import mc.obliviate.masterduels.queue.DuelQueueHandler;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.metrics.Metrics;
import mc.obliviate.masterduels.utils.optimization.ArenaWorldOptimizerHandler;
import mc.obliviate.masterduels.utils.scoreboard.InternalScoreboardManager;
import mc.obliviate.masterduels.utils.tab.TABManager;
import mc.obliviate.masterduels.utils.timer.GameHistoryCacheTimer;
import mc.obliviate.masterduels.utils.versioncontroller.ServerVersionController;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import static mc.obliviate.masterduels.VaultUtil.vaultEnabled;

public class MasterDuels extends JavaPlugin {

	private static boolean shutdownMode = false;
	private static Economy economy;
	private static Permission permissions;
	private final ArenaWorldOptimizerHandler worldOptimizerHandler = new ArenaWorldOptimizerHandler();
	private final SQLManager sqlManager = new SQLManager(this);
	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private final YamlStorageHandler yamlStorageHandler = new YamlStorageHandler(this);
	private final DuelQueueHandler duelQueueHandler = new DuelQueueHandler(this);
	private IArenaClearHandler arenaClearHandler;
	private InternalScoreboardManager scoreboardManager;

	public static MasterDuels getInstance() {
		return JavaPlugin.getPlugin(MasterDuels.class);
	}

	public static boolean isInShutdownMode() {
		return shutdownMode;
	}

	protected static Economy getEconomy() {
		return economy;
	}

	protected static Permission getPermissions() {
		return permissions;
	}

	@Override
	public void onEnable() {
		Logger.debug("Master Duels v" + getDescription().getVersion() + " loading process initializing...");
		Logger.debug("Obfuscate: " + checkObfuscated());
		Bukkit.getLogger().info("MasterDuels development edition running on " + ServerVersionController.getServerVersion() + " - build " + getDescription().getVersion());

		setupHandlers();
		registerListeners();
		registerCommands();
		setupTimers();
		loadKits();

		shutdownMode = false;
		startMetrics();

	}

	private void startMetrics() {
		new Metrics(this, 14587);
	}

	private boolean checkObfuscated() {
		try {
			String pack = "mc";
			pack = pack + ".obliviate";
			pack = pack + ".masterduels";
			pack = pack + ".VaultUtil";
			Class.forName(pack);
			return false;
		} catch (ClassNotFoundException e) {
			return true;
		}
	}

	private void setupHandlers() {
		new TABManager(this);
		yamlStorageHandler.init();
		inventoryAPI.init();
		setupArenaClearHandler();
		HCore.initialize(this);
		scoreboardManager = new InternalScoreboardManager(this);
		duelQueueHandler.init();
		if (YamlStorageHandler.getConfig().getBoolean("optimize-duel-worlds", false)) {
			worldOptimizerHandler.init();
		}
		sqlManager.init();
		setupVaultUtils();

		Logger.setDebugModeEnabled(YamlStorageHandler.getConfig().getBoolean("debug", false));
	}

	private void setupArenaClearHandler() {
		final String mode = YamlStorageHandler.getConfig().getString("arena-regeneration.mode", "SMART");
		//SMART
		if (!("ROLLBACKCORE".equals(mode) || "SLIMEWORLD".equals(mode) || "DISABLED".equals(mode))) {
			arenaClearHandler = new SmartArenaClearHandler(this);
			Bukkit.getPluginManager().registerEvents(new ArenaClearListener(this), this);
			arenaClearHandler.init();
		}
	}

	private void setupVaultUtils() {
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			vaultEnabled = true;
		}
		if (!setupPermissions()) {
			Logger.warn("MasterDuels could not find Vault plugin. All permissions will be checked as OP permission.");
		}
		if (!setupEconomy()) {
			Logger.warn("MasterDuels could not find Vault plugin. All players will authorized for economy activities.");
		}
	}

	private void setupTimers() {
		if (MatchHistoryLog.GAME_HISTORY_LOG_ENABLED) {
			new GameHistoryCacheTimer().init(this);
		}
	}

	private void registerCommands() {
		safeRegisterCommand("duel", new DuelCMD(this));
		safeRegisterCommand("dueladmin", new DuelAdminCMD(this));
	}

	private void safeRegisterCommand(String commandName, CommandExecutor executor) {
		final PluginCommand command = getCommand(commandName);
		if (command == null) {
			return;
		}
		command.setExecutor(executor);
	}

	private void registerListeners() {
		//RollbackListener registering from ArenaClear.java
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new DuelProtectListener(this), this);
		Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
		Bukkit.getPluginManager().registerEvents(new TeleportListener(), this);
		Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
		Bukkit.getPluginManager().registerEvents(new MatchRuleListener(), this);
		Bukkit.getPluginManager().registerEvents(new DeveloperCMD(this), this);
		Bukkit.getPluginManager().registerEvents(new CMDExecutorListener(this), this);
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
		for (final Match game : DataHandler.getArenas().values()) {
			if (game != null) {
				game.uninstall();
			}
		}
		getSqlManager().disconnect();
	}

	private boolean setupEconomy() {
		final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}

	private boolean setupPermissions() {
		final RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		permissions = rsp.getProvider();
		return permissions != null;
	}

	public YamlStorageHandler getDatabaseHandler() {
		return yamlStorageHandler;
	}

	public InventoryAPI getInventoryAPI() {
		return inventoryAPI;
	}

	public InternalScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}


	public SQLManager getSqlManager() {
		return sqlManager;
	}

	public IArenaClearHandler getArenaClearHandler() {
		return arenaClearHandler;
	}
}
