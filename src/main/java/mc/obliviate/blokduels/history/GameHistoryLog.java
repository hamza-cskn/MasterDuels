package mc.obliviate.blokduels.history;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.utils.serializer.SerializerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameHistoryLog implements HistoryLog {

	public static final List<GameHistoryLog> historyCache = new ArrayList<>();
	private final UUID uuid;
	private long startTime = 0L;
	private long endTime = 0L;
	private List<UUID> losers = null;
	private List<UUID> winners = null;

	public GameHistoryLog(final UUID uuid, long startTime, long endTime, List<UUID> losers, List<UUID> winners) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.losers = losers;
		this.winners = winners;
		this.uuid = uuid;
	}

	public GameHistoryLog() {
		this(UUID.randomUUID(), 0L, 0L, null, null);
	}

	public static GameHistoryLog deserialize(String serializedString) {
		final String[] objects = serializedString.split(SerializerUtils.OBJECT_SPLIT_CHARACTER);

		final int startTime = Integer.parseInt(objects[0]);
		final int gameTime = Integer.parseInt(objects[1]);
		final List<UUID> losers = SerializerUtils.deserializeUUIDList(objects[2]);
		final List<UUID> winners = SerializerUtils.deserializeUUIDList(objects[3]);

		return new GameHistoryLog(null, startTime, gameTime, losers, winners);

	}

	public String serialize() {
		final StringBuilder builder = new StringBuilder();

		builder.append(startTime).append(SerializerUtils.OBJECT_SPLIT_CHARACTER);
		builder.append(endTime).append(SerializerUtils.OBJECT_SPLIT_CHARACTER);
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

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void save(final BlokDuels plugin) {
		plugin.getSqlManager().appendDuelHistory(this);
	}
}
