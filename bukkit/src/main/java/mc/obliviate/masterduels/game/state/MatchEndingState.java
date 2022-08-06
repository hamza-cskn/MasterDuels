package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchEndEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchDataStorage;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.game.Team;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import static mc.obliviate.masterduels.kit.Kit.USE_PLAYER_INVENTORIES;

public class MatchEndingState implements MatchState {

	private final boolean naturalEnding;
	private final Match match;

	public MatchEndingState(Match match) {
		this(match, true);
	}

	public MatchEndingState(Match match, boolean naturalEnding) {
		this.naturalEnding = naturalEnding;
		this.match = match;
		init();
	}

	private void init() {
		Bukkit.getPluginManager().callEvent(new DuelMatchEndEvent(match, this, naturalEnding));

		match.getGameTaskManager().cancelTask("REMAINING_TIME");

		match.getGameDataStorage().setFinishTime(System.currentTimeMillis() + MatchDataStorage.getEndDelay().toMillis());
		match.getGameTaskManager().delayedTask("uninstall", this::next, MatchDataStorage.getEndDelay().getSeconds() * 20);

		match.broadcastGameEnd();
	}

	@Override
	public void next() {
		if (!match.getMatchState().equals(this)) return;
		match.setGameState(new MatchUninstallingState(match));
	}

	@Override
	public void leave(Member member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		Bukkit.getPluginManager().callEvent(new DuelMatchMemberLeaveEvent(member));
		InventoryStorer.restore(member.getPlayer());
		member.getMatch().removeMember(member);

		if (!USE_PLAYER_INVENTORIES && !InventoryStorer.restore(member.getPlayer())) {
			Logger.severe("inventory could not restored: " + member.getPlayer());
		}

		if (member.getPlayer().isOnline()) {
			match.showAll(member.getPlayer());
			Match.RESET_WHEN_PLAYER_LEFT.reset(member.getPlayer());
			Utils.teleportToLobby(member.getPlayer());
			MessageUtils.sendMessage(member.getPlayer(), "you-left-from-duel");
		}

	}

	@Override
	public MatchStateType getMatchStateType() {
		return MatchStateType.MATCH_ENDING;
	}

	public void dropItemsOfLosers(Location loc) {
		if (!USE_PLAYER_INVENTORIES) return;
		for (final Team team : match.getGameDataStorage().getGameTeamManager().getTeams()) {
			if (match.getGameDataStorage().getGameTeamManager().checkTeamEliminated(team)) {
				for (final Member member : team.getMembers()) {
					//fixme offline players inventory is not able to get.
					match.dropItems(member.getPlayer(), loc);
				}
			}
		}
	}

	@Override
	public Match getMatch() {
		return match;
	}

}
