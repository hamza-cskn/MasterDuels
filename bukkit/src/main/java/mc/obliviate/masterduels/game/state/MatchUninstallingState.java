package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchUninstallEvent;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.user.spectator.Spectator;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import org.bukkit.Bukkit;

import static mc.obliviate.masterduels.kit.Kit.USE_PLAYER_INVENTORIES;

public class MatchUninstallingState implements MatchState {

	private final Match match;

	public MatchUninstallingState(Match match) {
		this.match = match;
		init();
	}

	private void init() {
		Bukkit.getPluginManager().callEvent(new DuelMatchUninstallEvent(match, this));
		match.broadcastInGame("game-finished");

		for (final Member member : match.getAllMembers()) {
			leave(member);
		}
		for (final Spectator spectator : match.getGameSpectatorManager().getPureSpectatorStorage().getSpectatorList()) {
			leave(spectator);
		}

		match.getGameTaskManager().cancelTasks();
		DataHandler.registerArena(match.getArena());
		Logger.debug(Logger.DebugPart.GAME, "uninstall game - process finished");
	}

	@Override
	public void next() {

	}

	@Override
	public void leave(Member member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		Bukkit.getPluginManager().callEvent(new DuelMatchMemberLeaveEvent(member));
		DataHandler.getUsers().remove(member.getPlayer().getUniqueId());
		member.getMatch().getGameDataStorage().getGameTeamManager().unregisterMember(member);

		if (member.getPlayer().isOnline()) {
			if (!USE_PLAYER_INVENTORIES && !InventoryStorer.restore(member.getPlayer())) {
				Logger.severe("inventory could not restored: " + member.getPlayer());
			}

			match.showAll(member.getPlayer());
			Match.RESET_WHEN_PLAYER_LEFT.reset(member.getPlayer());
			Utils.teleportToLobby(member.getPlayer());
			MessageUtils.sendMessage(member.getPlayer(), "you-left-from-duel");
		}
	}

	@Override
	public Match getMatch() {
		return null;
	}

	@Override
	public MatchStateType getMatchStateType() {
		return MatchStateType.UNINSTALLING;
	}
}
