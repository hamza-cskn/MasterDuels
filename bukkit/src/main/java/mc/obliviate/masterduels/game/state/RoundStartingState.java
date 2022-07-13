package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.arena.elements.Positions;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.game.Team;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.notify.NotifyActionStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.time.Duration;
import java.util.ArrayList;

import static mc.obliviate.masterduels.kit.Kit.USE_PLAYER_INVENTORIES;

public class RoundStartingState implements MatchState {

	private static Duration LOCK_DURATION = Duration.ofSeconds(3);
	private static int LOCK_FREQUENCY = 5;
	private long roundStartTime;
	private final Match match;

	public RoundStartingState(Match match) {
		this.match = match;
		init();
	}

	public static void setLockFrequency(int lockFrequency) {
		if (lockFrequency > 0)
			LOCK_FREQUENCY = lockFrequency;
	}

	public static void setLockDuration(Duration lockDuration) {
		LOCK_DURATION = lockDuration;
	}

	private void init() {
		//note, this state is prev of round start
		roundStartTime = System.currentTimeMillis() + LOCK_DURATION.toMillis();

		match.resetPlayers();

		for (final Team team : match.getGameDataStorage().getGameTeamManager().getTeams()) {
			lockTeam(team);
		}

		match.getGameTaskManager().delayedTask("game-start", this::next, LOCK_DURATION.toSeconds() * 20L);
	}

	@Override
	public void next() {
		if (!match.getMatchState().equals(this)) return;
		match.setGameState(new PlayingState(match));
	}

	@Override
	public void leave(final Member member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		Bukkit.getPluginManager().callEvent(new DuelMatchMemberLeaveEvent(member));
		InventoryStorer.restore(member.getPlayer());
		member.getMatch().removeMember(member);

		if (!member.getPlayer().isOnline()) return;
		if (!USE_PLAYER_INVENTORIES && !InventoryStorer.restore(member.getPlayer())) {
			Logger.severe("inventory could not restored: " + member.getPlayer());
		}

		match.showAll(member.getPlayer());
		Match.RESET_WHEN_PLAYER_LEFT.reset(member.getPlayer());
		Utils.teleportToLobby(member.getPlayer());
		MessageUtils.sendMessage(member.getPlayer(), "you-left-from-duel");

		if (match.getAllMembers().size() > 0) {
			Logger.debug(Logger.DebugPart.GAME, "Game finishing...");
			match.finish();
		}

	}

	private void lockTeam(final Team team) {
		updateLock(team, (int) (LOCK_FREQUENCY * LOCK_DURATION.toSeconds()));
	}

	private void teleportToLockPosition(final Team team) {
		int i = 1;
		final Positions positions = match.getArena().getPositions().get("spawn-team-" + team.getTeamId());
		if (positions == null) {
			Logger.severe("Player could not teleported to lock position because location set is null.");
			return;
		}
		for (final Member member : new ArrayList<>(team.getMembers())) {
			final Location loc = positions.getLocation(i++);
			if (loc == null) {
				Logger.severe("Player could not teleported to lock position because location is null.");
			}
			final boolean teleportResult = member.getPlayer().teleport(loc);
			if (!teleportResult) {
				Logger.error("Player " + member.getPlayer().getName() + " could not teleported to duel arena. Game has been cancelled.");
				match.uninstall();
			}

		}
	}


	/**
	 * Recursion loop function to lock players
	 *
	 * @param team
	 * @param updateNo
	 */
	public void updateLock(final Team team, final int updateNo) {
		if (updateNo <= 0) {
			runNotifyActionForTeam(team);
			return;
		}

		if (updateNo % LOCK_FREQUENCY == 0) {
			runNotifyActionForTeam(team);
		}

		teleportToLockPosition(team);

		match.getGameTaskManager().delayedTask("lock.team-" + team.getTeamId(), () -> {
			updateLock(team, updateNo - 1);
		}, 20 / LOCK_FREQUENCY);
	}

	private void runNotifyActionForTeam(Team team) {
		NotifyActionStack notifyAction = getNotifyAction(Math.max(roundStartTime - System.currentTimeMillis(), 0));
		if (notifyAction != null) {
			for (Member member : team.getMembers()) {
				notifyAction.run(member.getPlayer());
			}
		}

	}

	private NotifyActionStack getNotifyAction(long remainingTime) {
		return NotifyActionStack.getActionAt(remainingTime);
	}

	@Override
	public MatchStateType getMatchStateType() {
		return MatchStateType.ROUND_STARTING;
	}

	@Override
	public Match getMatch() {
		return match;
	}
}
