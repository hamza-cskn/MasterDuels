package mc.obliviate.masterduels.arenaclear;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.game.Game;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class ArenaClearHandler {

	private final Map<String, ArenaClear> arenaClearMap = new HashMap<>();
	private final MasterDuels plugin;

	public ArenaClearHandler(MasterDuels plugin) {
		this.plugin = plugin;
	}

	public void init() {
		Bukkit.getPluginManager().registerEvents(new RollbackListener(plugin, plugin.getDatabaseHandler().getConfig().getBoolean("prevent-break-non-placed-blocks", true)), plugin);
	}

	public void add(Game game, MasterDuels plugin) {
		arenaClearMap.put(game.getArena().getName(), new ArenaClear(plugin, game.getArena()));
	}

	public Map<String, ArenaClear> getArenaClearMap() {
		return arenaClearMap;
	}

	public ArenaClear getArenaClear(String arenaName) {
		return arenaClearMap.get(arenaName);
	}


}
