package mc.obliviate.blokduels;

import mc.obliviate.blokduels.commands.DuelCMD;
import mc.obliviate.blokduels.commands.DuelArenasCMD;
import mc.obliviate.blokduels.commands.KitEditorCMD;
import mc.obliviate.blokduels.commands.SetupCMD;
import mc.obliviate.blokduels.config.ConfigHandler;
import mc.obliviate.blokduels.data.DatabaseHandler;
import mc.obliviate.blokduels.listeners.ChatListener;
import mc.obliviate.blokduels.listeners.DuelProtectListener;
import mc.obliviate.blokduels.listeners.PlayerConnectionListener;
import mc.obliviate.blokduels.listeners.PreDeathListener;
import mc.obliviate.blokduels.utils.scoreboard.ScoreboardManager;
import mc.obliviate.inventory.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BlokDuels extends JavaPlugin {

	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private final DatabaseHandler databaseHandler = new DatabaseHandler(this);
	private final ConfigHandler configHandler = new ConfigHandler(this);
	private ScoreboardManager scoreboardManager;

	@Override
	public void onEnable() {

		registerListeners();
		registerCommands();

		databaseHandler.init();
		inventoryAPI.init();
		configHandler.init();
		scoreboardManager = new ScoreboardManager(this);
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
		Bukkit.getPluginManager().registerEvents(new PreDeathListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
	}

	@Override
	public void onDisable() {

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
}
