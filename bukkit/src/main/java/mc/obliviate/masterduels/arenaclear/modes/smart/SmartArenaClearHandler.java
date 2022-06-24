package mc.obliviate.masterduels.arenaclear.modes.smart;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.IGame;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.arenaclear.IArenaClearHandler;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class SmartArenaClearHandler implements IArenaClearHandler {

	private final Map<String, SmartArenaClear> arenaClearMap = new HashMap<>();
	private final MasterDuels plugin;

	public SmartArenaClearHandler(MasterDuels plugin) {
		this.plugin = plugin;
	}

	public void init() {
		Bukkit.getPluginManager().registerEvents(new RollbackListener(plugin, plugin.getDatabaseHandler().getConfig().getBoolean("prevent-break-non-placed-blocks", true)), plugin);
	}

	public void add(IGame game, MasterDuels plugin) {
		arenaClearMap.put(game.getArena().getName(), new SmartArenaClear(plugin, (Arena) game.getArena()));
	}

	public Map<String, SmartArenaClear> getArenaClearMap() {
		return arenaClearMap;
	}

	public SmartArenaClear getArenaClear(String arenaName) {
		return arenaClearMap.get(arenaName);
	}


}
