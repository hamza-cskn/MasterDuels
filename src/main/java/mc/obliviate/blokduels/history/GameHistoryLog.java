package mc.obliviate.blokduels.history;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.user.team.Member;
import mc.obliviate.blokduels.user.team.Team;
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

	public void finish(Game game) {
		//save history log
		setEndTime(System.currentTimeMillis());

		final List<UUID> losers = new ArrayList<>();
		final List<UUID> winners = new ArrayList<>();
		for (final Team team : game.getTeams().values()) {
			final List<UUID> list;

			if (game.checkTeamEliminated(team)) list = losers;
			else list = winners;

			for (final Member member : team.getMembers()) {
				list.add(member.getPlayer().getUniqueId());
			}
		}
		setLosers(losers);
		setWinners(winners);
		save(game.getPlugin());

		//save statistics
		for (final UUID winner : winners) {
			game.getPlugin().getSqlManager().addWin(winner, 1);
		}
		for (final UUID loser : losers) {
			game.getPlugin().getSqlManager().addLose(loser, 1);
		}
		setWinners(winners);
		setLosers(losers);
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
