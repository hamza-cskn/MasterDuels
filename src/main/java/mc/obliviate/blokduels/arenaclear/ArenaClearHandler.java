package mc.obliviate.blokduels.arenaclear;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.game.Game;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class ArenaClearHandler {

	private final Map<String, ArenaClear> arenaClearMap = new HashMap<>();
	private final BlokDuels plugin;

	public ArenaClearHandler(BlokDuels plugin) {
		this.plugin = plugin;
	}

	public void init() {
		Bukkit.getPluginManager().registerEvents(new RollbackListener(plugin), plugin);
	}

	public void add(Game game, BlokDuels plugin) {
		arenaClearMap.put(game.getArena().getName(), new ArenaClear(plugin, game.getArena()));
	}

	public Map<String, ArenaClear> getArenaClearMap() {
		return arenaClearMap;
	}

	public ArenaClear getArenaClear(String arenaName) {
		return arenaClearMap.get(arenaName);
	}


}
