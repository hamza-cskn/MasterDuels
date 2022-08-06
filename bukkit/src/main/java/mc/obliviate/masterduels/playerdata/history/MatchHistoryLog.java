package mc.obliviate.masterduels.playerdata.history;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MatchHistoryLog implements Serializable {

	//active listening logs
	private static final Map<Match, MatchHistoryLog> SAVING_MATCH_HISTORY_LOGS = new HashMap<>();
	//player uuid, player history log
	private final Map<UUID, PlayerHistoryLog> playerHistoryLogMap;
	private long startTime = -1;
	private long finishTime = -1;
	private List<UUID> winners;
	private transient final Match match;

	private int playedRound;
	private int maxRound;

	public MatchHistoryLog(Map<UUID, PlayerHistoryLog> playerHistoryLogMap, long startTime, long finishTime, List<UUID> winners) {
		this.playerHistoryLogMap = playerHistoryLogMap;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.winners = winners;
		this.match = null;
	}

	public MatchHistoryLog(final Match match) {
		this.match = match;
		this.playerHistoryLogMap = new HashMap<>();
	}

	public void start() {
		Preconditions.checkState(startTime < 0, "histories cannot be started two times.");
		Preconditions.checkState(match != null, "match cannot be null.");
		this.startTime = System.currentTimeMillis();
		SAVING_MATCH_HISTORY_LOGS.put(match, this);

		maxRound = match.getGameDataStorage().getGameRoundData().getTotalRounds();

		for (final Member member : match.getAllMembers()) {
			final PlayerHistoryLog playerLog = new PlayerHistoryLog();
			playerHistoryLogMap.put(member.getPlayer().getUniqueId(), playerLog);

			final Player player = member.getPlayer();

			playerLog.setKitName(member.getKit() == null ? null : member.getKit().getKitName());
			playerLog.getPlayerData().setJump(player.getStatistic(Statistic.JUMP) * -1);
			playerLog.getPlayerData().setFall(player.getStatistic(Statistic.FALL_ONE_CM) * -1);
			playerLog.getPlayerData().setSprint(player.getStatistic(Statistic.SPRINT_ONE_CM) * -1);
			playerLog.getPlayerData().setWalk(player.getStatistic(Statistic.WALK_ONE_CM) * -1);
			playerLog.getPlayerData().setDamageTaken(player.getStatistic(Statistic.DAMAGE_TAKEN) * -1);

		}
	}

	public void finish(final List<UUID> winners) {
		Preconditions.checkState(finishTime < 0, "histories cannot be finished two times.");
		this.finishTime = System.currentTimeMillis();
		this.winners = winners;

		playedRound = match.getGameDataStorage().getGameRoundData().getCurrentRound();

		for (final Member member : match.getAllMembers()) {
			final PlayerHistoryLog playerLog = playerHistoryLogMap.get(member.getPlayer().getUniqueId());

			if (playerLog == null) continue;
			final Player player = member.getPlayer();

			playerLog.getPlayerData().setJump(playerLog.getPlayerData().getJump() + player.getStatistic(Statistic.JUMP));
			playerLog.getPlayerData().setFall(playerLog.getPlayerData().getFall() + player.getStatistic(Statistic.FALL_ONE_CM));
			playerLog.getPlayerData().setSprint(playerLog.getPlayerData().getSprint() + player.getStatistic(Statistic.SPRINT_ONE_CM));
			playerLog.getPlayerData().setWalk(playerLog.getPlayerData().getWalk() + player.getStatistic(Statistic.WALK_ONE_CM));
			playerLog.getPlayerData().setDamageTaken(playerLog.getPlayerData().getDamageTaken() + player.getStatistic(Statistic.DAMAGE_TAKEN));

		}
	}

	public void uninstall(boolean naturalUninstall) {
		SAVING_MATCH_HISTORY_LOGS.remove(match);
	}

	public Map<UUID, PlayerHistoryLog> getPlayerHistoryLogMap() {
		return playerHistoryLogMap;
	}

	protected static Map<Match, MatchHistoryLog> getSavingMatchHistoryLogs() {
		return Collections.unmodifiableMap(SAVING_MATCH_HISTORY_LOGS);
	}

	public static PlayerHistoryLog getPlayerHistory(Player player) {
		final IUser user = UserHandler.getUser(player.getUniqueId());
		Match match;
		if (user instanceof Member) {
			match = ((Member) user).getMatch();
		} else if (user instanceof Spectator) {
			match = ((Spectator) user).getMatch();
		} else {
			return null;
		}

		final MatchHistoryLog log = SAVING_MATCH_HISTORY_LOGS.get(match);
		if (log == null) return null;
		return log.getPlayerHistoryLogMap().get(player.getUniqueId());
	}

	public int getMaxRound() {
		return maxRound;
	}

	public int getPlayedRound() {
		return playedRound;
	}

	public List<UUID> getWinners() {
		return winners;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public Match getMatch() {
		return match;
	}
}
