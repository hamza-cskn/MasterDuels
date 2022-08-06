package mc.obliviate.masterduels.playerdata.history;

import mc.obliviate.masterduels.playerdata.PlayerData;

import java.io.Serializable;

public class PlayerHistoryLog implements Serializable {

	private final PlayerData playerData;
	private String kitName;

	public PlayerHistoryLog() {
		this(new PlayerData());
	}

	public PlayerHistoryLog(PlayerData playerData) {
		this.playerData = playerData;
	}

	public PlayerData getPlayerData() {
		return playerData;
	}

	public String getKitName() {
		return kitName;
	}

	public void setKitName(String kitName) {
		this.kitName = kitName;
	}
}
