package mc.obliviate.blokduels;

import mc.obliviate.blokduels.commands.DuelCMD;
import mc.obliviate.blokduels.commands.DuelArenasCMD;
import mc.obliviate.blokduels.commands.SetupCMD;
import mc.obliviate.blokduels.data.DatabaseHandler;
import mc.obliviate.blokduels.listeners.ChatListener;
import mc.obliviate.blokduels.listeners.DuelProtectListener;
import mc.obliviate.blokduels.listeners.PreDeathListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.efekurbann.inventory.InventoryAPI;

public class BlokDuels extends JavaPlugin {

	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private final DatabaseHandler databaseHandler = new DatabaseHandler(this);

	@Override
	public void onEnable() {

		registerListeners();
		registerCommands();

		databaseHandler.init();
		inventoryAPI.init();
	}
	private void registerCommands() {
		getCommand("duel").setExecutor(new DuelCMD(this));
		getCommand("duelarenas").setExecutor(new DuelArenasCMD(this));
		getCommand("duelsetup").setExecutor(new SetupCMD(this));
	}

	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new ChatListener(),this);
		Bukkit.getPluginManager().registerEvents(new DuelProtectListener(),this);
		Bukkit.getPluginManager().registerEvents(new PreDeathListener(),this);
	}

	@Override
	public void onDisable() {

	}
}
