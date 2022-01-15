package mc.obliviate.masterduels;

import com.hakan.messageapi.bukkit.MessageAPI;
import mc.obliviate.masterduels.arenaclear.ArenaClearListener;
import mc.obliviate.masterduels.arenaclear.IArenaClearHandler;
import mc.obliviate.masterduels.arenaclear.modes.smart.SmartArenaClearHandler;
import mc.obliviate.masterduels.commands.DeveloperCMD;
import mc.obliviate.masterduels.commands.DuelCMD;
import mc.obliviate.masterduels.commands.DuelAdminCMD;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.data.YamlStorageHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.bet.Bet;
import mc.obliviate.masterduels.game.gamerule.GameRuleListener;
import mc.obliviate.masterduels.kit.serializer.KitSerializer;
import mc.obliviate.masterduels.listeners.*;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.scoreboard.ScoreboardManager;
import mc.obliviate.masterduels.utils.tab.TABManager;
import mc.obliviate.masterduels.utils.timer.SQLCacheTimer;
import mc.obliviate.inventory.InventoryAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import static mc.obliviate.masterduels.VaultUtil.vaultEnabled;

public class MasterDuels extends JavaPlugin {

	private static boolean shutdownMode = false;
	private final SQLManager sqlManager = new SQLManager(this);
	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private IArenaClearHandler arenaClearHandler;
	private final YamlStorageHandler yamlStorageHandler = new YamlStorageHandler(this);
	private MessageAPI messageAPI;
	private ScoreboardManager scoreboardManager;
	private static Economy economy;
	private static Permission permissions;

	public static boolean isInShutdownMode() {
		return shutdownMode;
	}

	@Override
	public void onEnable() {
		setupHandlers();
		registerListeners();
		registerCommands();
		setupTimers();
		loadKits();
		shutdownMode = false;
	}

	private void setupHandlers() {
		yamlStorageHandler.init();
		inventoryAPI.init();
		switch (yamlStorageHandler.getConfig().getString("arena-regeneration.mode", "SMART")) {
			case "ROLLBACKCORE":
			case "SLIMEWORLD":
			case "DISABLED":
				break;
			default: //SMART
				arenaClearHandler = new SmartArenaClearHandler(this);
			Bukkit.getPluginManager().registerEvents(new ArenaClearListener(this), this);
			arenaClearHandler.init();
		}
		scoreboardManager = new ScoreboardManager(this);
		messageAPI = MessageAPI.getInstance(this);
		new TABManager(this);
		sqlManager.init();

		setupVaultUtils();

	}

	private void setupVaultUtils() {
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			vaultEnabled = true;
		}
		if (!setupPermissions()) {
			Logger.warn("MasterDuels could not find Vault plugin. All permissions will be checked as OP permission.");
		}
		if (!setupEconomy()) {
			Bet.betsEnabled = false; //disable bets
			Logger.warn("MasterDuels could not find Vault plugin. All players will authorized for economy activities.");
		}
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
		Bukkit.getPluginManager().registerEvents(new DeveloperCMD(this), this);
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

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public MessageAPI getMessageAPI() {
		return messageAPI;
	}

	public SQLManager getSqlManager() {
		return sqlManager;
	}

	public IArenaClearHandler getArenaClearHandler() {
		return arenaClearHandler;
	}

	protected static Economy getEconomy() {
		return economy;
	}

	protected static Permission getPermissions() {
		return permissions;
	}
}
