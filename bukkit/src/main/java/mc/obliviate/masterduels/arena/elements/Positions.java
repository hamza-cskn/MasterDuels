package mc.obliviate.masterduels.arena.elements;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Purpose of this class is,
 * storing location groups.
 *
 * ex.
 * spawn positions of a team
 */

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
