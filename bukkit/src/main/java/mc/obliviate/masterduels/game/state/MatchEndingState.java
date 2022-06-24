package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.arena.MatchStateType;
import mc.obliviate.masterduels.api.events.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.events.arena.DuelMatchFinishEvent;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchDataStorage;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import static mc.obliviate.masterduels.kit.Kit.USE_PLAYER_INVENTORIES;

public class MatchEndingState implements MatchState {

	private final Match match;

	public MatchEndingState(Match match) {
		this.match = match;
		init();
	}

	private void init() {
		Bukkit.getPluginManager().callEvent(new DuelMatchFinishEvent(match));

		match.getGameTaskManager().cancelTask("REMAINING_TIME");

		match.getGameDataStorage().setFinishTime(System.currentTimeMillis() + MatchDataStorage.getEndDelay().toMillis());
		match.getGameTaskManager().delayedTask("uninstall", match::uninstall, MatchDataStorage.getEndDelay().toMillis());

		match.broadcastGameEnd();
		Logger.debug(Logger.DebugPart.GAME, "finish game - process finished");
	}

	@Override
	public void next() {
		match.setGameState(new MatchUninstallingState(match));
	}

	@Override
	public void leave(IMember member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		Bukkit.getPluginManager().callEvent(new DuelMatchMemberLeaveEvent(member));
		DataHandler.getUsers().remove(member.getPlayer().getUniqueId());
		member.getTeam().unregisterMember(member);

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
		return MatchStateType.GAME_ENDING;
	}

	public void dropItemsOfLosers(Location loc) {
		if (!USE_PLAYER_INVENTORIES) return;
		for (final ITeam team : match.getGameDataStorage().getGameTeamManager().getTeams()) {
			if (match.getGameDataStorage().getGameTeamManager().checkTeamEliminated(team)) {
				for (final IMember member : team.getMembers()) {
					//fixme offline players inventory is not able to get.
					match.dropItems(member.getPlayer(), loc);
				}
			}
		}
	}

	@Override
	public Match getMatch() {
		return null;
	}

}
