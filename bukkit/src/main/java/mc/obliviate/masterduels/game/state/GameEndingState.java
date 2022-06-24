package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.arena.GameStateType;
import mc.obliviate.masterduels.api.events.arena.DuelGameFinishEvent;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameDataStorage;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import static mc.obliviate.masterduels.kit.Kit.USE_PLAYER_INVENTORIES;

public class GameEndingState implements GameState {

	private final Game match;

	public GameEndingState(Game match) {
		this.match = match;
		init();
	}

	private void init() {
		Bukkit.getPluginManager().callEvent(new DuelGameFinishEvent(match));

		match.getGameTaskManager().cancelTask("REMAINING_TIME");

		match.getGameDataStorage().setFinishTime(System.currentTimeMillis() + GameDataStorage.getEndDelay().toMillis());
		match.getGameTaskManager().delayedTask("uninstall", match::uninstall, GameDataStorage.getEndDelay().toMillis());

		match.broadcastGameEnd();
		Logger.debug(Logger.DebugPart.GAME, "finish game - process finished");
	}

	@Override
	public void next() {
		match.setGameState(new GameUninstallingState(match));
	}

	@Override
	public void leave(IMember member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		DataHandler.getUsers().remove(member.getPlayer().getUniqueId());
		member.getTeam().unregisterMember(member);

		if (!USE_PLAYER_INVENTORIES && !InventoryStorer.restore(member.getPlayer())) {
			Logger.severe("inventory could not restored: " + member.getPlayer());
		}

		if (member.getPlayer().isOnline()) {
			match.showAll(member.getPlayer());
			Game.RESET_WHEN_PLAYER_LEFT.reset(member.getPlayer());
			Utils.teleportToLobby(member.getPlayer());
			MessageUtils.sendMessage(member.getPlayer(), "you-left-from-duel");
		}

	}

	@Override
	public GameStateType getGameStateType() {
		return GameStateType.GAME_ENDING;
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
	public Game getMatch() {
		return null;
	}

}
