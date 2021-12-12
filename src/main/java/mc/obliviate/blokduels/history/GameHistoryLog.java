package mc.obliviate.blokduels.history;

import mc.obliviate.blokduels.utils.serializer.SerializerUtils;

import java.util.List;
import java.util.UUID;

public class GameHistoryLog implements HistoryLog {

	private long startTime = 0L;
	private int gameTime = 0;
	private List<UUID> losers = null;
	private List<UUID> winners = null;

	public GameHistoryLog(long startTime, int gameTime, List<UUID> losers, List<UUID> winners) {
		this.startTime = startTime;
		this.gameTime = gameTime;
		this.losers = losers;
		this.winners = winners;
	}

	public GameHistoryLog() {
	}

	//todo not tested
	public static GameHistoryLog deserialize(String serializedString) {
		final String[] objects = serializedString.split(SerializerUtils.OBJECT_SPLIT_CHARACTER);

		final int startTime = Integer.parseInt(objects[0]);
		final int gameTime = Integer.parseInt(objects[1]);
		final List<UUID> losers = SerializerUtils.deserializeUUIDList(objects[2]);
		final List<UUID> winners = SerializerUtils.deserializeUUIDList(objects[3]);

		return new GameHistoryLog(startTime, gameTime, losers, winners);

	}

	public String serialize() {
		final StringBuilder builder = new StringBuilder();

		builder.append(startTime).append(SerializerUtils.OBJECT_SPLIT_CHARACTER);
		builder.append(gameTime).append(SerializerUtils.OBJECT_SPLIT_CHARACTER);
		builder.append(SerializerUtils.serializeStringConvertableList(losers)).append(SerializerUtils.OBJECT_SPLIT_CHARACTER);
		builder.append(SerializerUtils.serializeStringConvertableList(winners)).append(SerializerUtils.OBJECT_SPLIT_CHARACTER);

		return builder.toString();

	}

	public List<UUID> getLosers() {
		return losers;
	}

	public void setLosers(List<UUID> losers) {
		this.losers = losers;
	}

	public List<UUID> getWinners() {
		return winners;
	}

	public void setWinners(List<UUID> winners) {
		this.winners = winners;
	}

	public int getGameTime() {
		return gameTime;
	}

	public void setGameTime(int gameTime) {
		this.gameTime = gameTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
}
