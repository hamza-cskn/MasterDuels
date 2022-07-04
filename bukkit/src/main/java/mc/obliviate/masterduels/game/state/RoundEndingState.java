package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import org.bukkit.Bukkit;

import static mc.obliviate.masterduels.kit.Kit.USE_PLAYER_INVENTORIES;

public class RoundEndingState implements MatchState {

	private final Match match;

	public RoundEndingState(Match match) {
		this.match = match;
		init();
	}

	private void init() {
		match.getGameTaskManager().delayedTask("next-round-delay", () -> {
			match.getGameSpectatorManager().getSemiSpectatorStorage().unspectateAll();
			match.resetPlayers();
			next();
		}, 10 * 20);
	}

	@Override
	public void next() {
		if (!match.getMatchState().equals(this)) return;
		match.setGameState(new RoundStartingState(match));
	}

	@Override
	public void leave(Member member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		Bukkit.getPluginManager().callEvent(new DuelMatchMemberLeaveEvent(member));
		member.getMatch().removeMember(member);

		if (!USE_PLAYER_INVENTORIES && !InventoryStorer.restore(member.getPlayer())) {
			Logger.severe("inventory could not restored: " + member.getPlayer());
		}

		if (member.getPlayer().isOnline()) {
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

	@Override
	public Match getMatch() {
		return match;
	}


	@Override
	public MatchStateType getMatchStateType() {
		return MatchStateType.ROUND_ENDING;
	}
}
