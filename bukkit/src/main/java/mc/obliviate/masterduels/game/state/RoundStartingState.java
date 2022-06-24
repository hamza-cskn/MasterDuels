package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.arena.MatchStateType;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.arena.elements.Positions;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import org.bukkit.Location;

import java.time.Duration;

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

		for (final ITeam team : match.getGameDataStorage().getGameTeamManager().getTeams()) {
			lockTeam(team);
		}

		match.getGameTaskManager().delayedTask("game-start", this::next, LOCK_DURATION.toSeconds() * 20L);
	}

	@Override
	public void next() {
		match.setGameState(new PlayingState(match));
	}

	@Override
	public void leave(final ISpectator spectator) {
		match.getGameSpectatorManager().unspectate(spectator);
		Match.RESET_WHEN_PLAYER_LEFT.reset(spectator.getPlayer());
		MessageUtils.sendMessage(spectator.getPlayer(), "you-left-from-duel");
		Utils.teleportToLobby(spectator.getPlayer());
	}

	@Override
	public void leave(final IMember member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		DataHandler.getUsers().remove(member.getPlayer().getUniqueId());
		member.getTeam().unregisterMember(member);

		if (member.getPlayer().isOnline()) {
			if (!USE_PLAYER_INVENTORIES && !InventoryStorer.restore(member.getPlayer())) {
				Logger.severe("inventory could not restored: " + member.getPlayer());
			}

			match.showAll(member.getPlayer());
			Match.RESET_WHEN_PLAYER_LEFT.reset(member.getPlayer());
			Utils.teleportToLobby(member.getPlayer());
			MessageUtils.sendMessage(member.getPlayer(), "you-left-from-duel");

			if (member.getTeam().getMembers().size() == 0 && match.getAllMembers().size() > 0) {
				Logger.debug(Logger.DebugPart.GAME, "Game finishing...");
				match.finish();
			}
		}
	}

	private void lockTeam(final ITeam team) {
		updateLock(team, (int) (LOCK_FREQUENCY * LOCK_DURATION.toSeconds()));
	}

	private void teleportToLockPosition(final ITeam team) {
		int i = 1;
		final Positions positions = match.getArena().getPositions().get("spawn-team-" + team.getTeamId());
		if (positions == null) {
			Logger.severe("Player could not teleported to lock position because location set is null.");
			return;
		}
		for (final IMember member : team.getMembers()) {
			final Location loc = positions.getLocation(i++);
			if (loc == null) {
				Logger.severe("Player could not teleported to lock position because location is null.");
			} else if (member.getPlayer().teleport(loc)) return;

			Logger.error("Player " + member.getPlayer().getName() + " could not teleported to duel arena. Game has been cancelled.");
			match.uninstall();
		}
	}


	/**
	 * Recursion loop function to lock players
	 *
	 * @param team
	 * @param updateNo
	 */
	public void updateLock(final ITeam team, final int updateNo) {
		if (updateNo <= 0) return;

		Object notifyAction = getNotifyAction(roundStartTime - System.currentTimeMillis());
		if (notifyAction != null) return; //do notify action

		teleportToLockPosition(team);

		match.getGameTaskManager().delayedTask("lock.team-" + team.getTeamId(), () -> {
			updateLock(team, updateNo - 1);
		}, 20 / LOCK_FREQUENCY);
	}

	public Object getNotifyAction(long remainingTime) { //todo notify action
		return null;
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
