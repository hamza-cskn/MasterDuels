package mc.obliviate.masterduels.history;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.user.team.Team;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GameHistoryLog implements HistoryLog {

	public static boolean GAME_HISTORY_LOG_ENABLED = true;
	public static final LinkedList<GameHistoryLog> historyCache = new LinkedList<>();
	private final UUID uuid;
	private long startTime;
	private long endTime;
	private List<UUID> losers;
	private List<UUID> winners;

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

	public void save(final MasterDuels plugin) {
		if (!GameHistoryLog.GAME_HISTORY_LOG_ENABLED) return; //object works but don't writes changes.
		plugin.getSqlManager().appendDuelHistory(this);
	}
}
