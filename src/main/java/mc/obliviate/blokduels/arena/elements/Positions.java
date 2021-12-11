package mc.obliviate.blokduels.arena.elements;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class Positions {

	private final Map<Integer, Location> locations = new HashMap<>();

	public Map<Integer, Location> getLocations() {
		return locations;
	}

	public void registerLocation(final int id, final Location loc) {
		locations.put(id, loc);
	}

	public Location getLocation(final int id) {
		return locations.get(id);
	}
}
