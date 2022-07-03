package mc.obliviate.masterduels.history;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.*;

public class MatchHistoryLog {

	//active listening logs
	private static final Map<Match, MatchHistoryLog> SAVING_MATCH_HISTORY_LOGS = new HashMap<>();
	//player uuid, player history log
	private final Map<UUID, PlayerHistoryLog> playerHistoryLogMap;
	private long startTime = -1;
	private long finishTime = -1;
	private List<UUID> winners;
	private final Match match;

	public MatchHistoryLog(final Match match) {
		this.match = match;
		this.playerHistoryLogMap = new HashMap<>();

	}

	public void start() {
		Preconditions.checkState(startTime < 0, "histories cannot be started two times.");
		this.startTime = System.currentTimeMillis();
		SAVING_MATCH_HISTORY_LOGS.put(match, this);

		for (final Member member : match.getAllMembers()) {
			final PlayerHistoryLog playerLog = new PlayerHistoryLog();
			playerHistoryLogMap.put(member.getPlayer().getUniqueId(), playerLog);

			final Player player = member.getPlayer();

			playerLog.setJump(player.getStatistic(Statistic.JUMP) * -1);
			playerLog.setFall(player.getStatistic(Statistic.FALL_ONE_CM) * -1);
			playerLog.setSprint(player.getStatistic(Statistic.SPRINT_ONE_CM) * -1);
			playerLog.setWalk(player.getStatistic(Statistic.WALK_ONE_CM) * -1);
			//playerLog.setBrokenBlocks(player.getStatistic(Statistic.MINE_BLOCK) * -1);
			playerLog.setDamageDealt(player.getStatistic(Statistic.DAMAGE_DEALT) * -1);
			playerLog.setDamageTaken(player.getStatistic(Statistic.DAMAGE_TAKEN) * -1);

		}
	}

	public void finish(final List<UUID> winners) {
		Preconditions.checkState(finishTime < 0, "histories cannot be finished two times.");
		this.finishTime = System.currentTimeMillis();
		this.winners = winners;

		for (final Member member : match.getAllMembers()) {
			final PlayerHistoryLog playerLog = playerHistoryLogMap.get(member.getPlayer().getUniqueId());

			if (playerLog == null) continue;
			final Player player = member.getPlayer();

			playerLog.setJump(playerLog.getJump() + player.getStatistic(Statistic.JUMP));
			playerLog.setFall(playerLog.getFall() + player.getStatistic(Statistic.FALL_ONE_CM));
			playerLog.setSprint(playerLog.getSprint() + player.getStatistic(Statistic.SPRINT_ONE_CM));
			playerLog.setWalk(playerLog.getWalk() + player.getStatistic(Statistic.WALK_ONE_CM));
			//playerLog.setBrokenBlocks(player.getStatistic(Statistic.MINE_BLOCK));
			playerLog.setDamageDealt(playerLog.getDamageDealt() + player.getStatistic(Statistic.DAMAGE_DEALT));
			playerLog.setDamageTaken(playerLog.getDamageTaken() + player.getStatistic(Statistic.DAMAGE_TAKEN));
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
		final Member member = UserHandler.getMember(player.getUniqueId());
		if (member == null) return null;

		final MatchHistoryLog log = SAVING_MATCH_HISTORY_LOGS.get(member.getMatch());
		if (log == null) return null;
		return log.getPlayerHistoryLogMap().get(player.getUniqueId());
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

}
