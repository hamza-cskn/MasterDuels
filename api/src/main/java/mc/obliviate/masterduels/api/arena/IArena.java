package mc.obliviate.masterduels.api.arena;

import org.bukkit.Location;

public interface IArena {

	boolean isEnabled();

	void setEnabled(boolean enabled);

	int getTeamAmount();

	int getTeamSize();

	String getName();

	String getMapName();

	Location getSpectatorLocation();

}
