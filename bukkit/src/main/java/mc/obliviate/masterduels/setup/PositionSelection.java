package mc.obliviate.masterduels.setup;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class PositionSelection {

	private Location pos1;
	private Location pos2;

	public Location getPos1() {
		return pos1;
	}

	public Location getPos2() {
		return pos2;
	}

	public void setPos1(Location pos1) {
		this.pos1 = pos1;
	}

	public void setPos2(Location pos2) {
		this.pos2 = pos2;
	}

	public static String formatLocation(final Location loc) {
		if (loc == null) return ChatColor.RED + "No Location";
		return loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
	}
}
