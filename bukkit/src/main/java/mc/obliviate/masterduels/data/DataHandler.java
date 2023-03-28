package mc.obliviate.masterduels.data;

import org.bukkit.Location;

public class DataHandler {

    private static Location lobbyLocation = null;

	public static Location getLobbyLocation() {
		return lobbyLocation;
	}

	public static void setLobbyLocation(Location lobbyLocation) {
		if (lobbyLocation == null) return;
		if (lobbyLocation.getWorld() == null) return;
		DataHandler.lobbyLocation = lobbyLocation;
	}

}
