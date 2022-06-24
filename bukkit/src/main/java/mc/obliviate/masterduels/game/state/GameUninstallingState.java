package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.arena.GameStateType;
import mc.obliviate.masterduels.api.events.arena.DuelArenaUninstallEvent;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import org.bukkit.Bukkit;

import static mc.obliviate.masterduels.kit.Kit.USE_PLAYER_INVENTORIES;

public class GameUninstallingState implements GameState {

	private final Game match;

	public GameUninstallingState(Game match) {
		this.match = match;
		init();
	}

	private void init() {
		Bukkit.getPluginManager().callEvent(new DuelArenaUninstallEvent(match));

		match.broadcastInGame("game-finished");

		for (final IMember member : match.getAllMembers()) {
			leave(member);
		}
		for (final ISpectator spectator : match.getGameSpectatorManager().getPureSpectatorStorage().getSpectatorList()) {
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
	public void leave(IMember member) {
		if (!member.getTeam().getMembers().contains(member)) return;

		DataHandler.getUsers().remove(member.getPlayer().getUniqueId());
		member.getTeam().unregisterMember(member);

		if (member.getPlayer().isOnline()) {
			if (!USE_PLAYER_INVENTORIES && !InventoryStorer.restore(member.getPlayer())) {
				Logger.severe("inventory could not restored: " + member.getPlayer());
			}

			match.showAll(member.getPlayer());
			Game.RESET_WHEN_PLAYER_LEFT.reset(member.getPlayer());
			Utils.teleportToLobby(member.getPlayer());
			MessageUtils.sendMessage(member.getPlayer(), "you-left-from-duel");
		}
	}

	@Override
	public Game getMatch() {
		return null;
	}

	@Override
	public GameStateType getGameStateType() {
		return GameStateType.UNINSTALLING;
	}
}
