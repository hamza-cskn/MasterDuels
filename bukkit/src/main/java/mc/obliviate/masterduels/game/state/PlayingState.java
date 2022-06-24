package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.arena.GameStateType;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.event.entity.EntityDamageEvent;

import static mc.obliviate.masterduels.kit.Kit.USE_PLAYER_INVENTORIES;

public class PlayingState implements GameState {

	private final Game match;

	public PlayingState(Game game) {
		this.match = game;
	}

	@Override
	public void next() {
		if (match.getGameDataStorage().getRoundData().nextRound()) {
			match.setGameState(new RoundEndingState(match));
		} else {
			match.setGameState(new GameEndingState(match));
		}

	}

	@Override
	public void onDamage(EntityDamageEvent event, IMember victim, IMember attacker) {
		if (attacker != null)
			attacker.getPlayer().sendMessage("You hitted to " + victim.getPlayer().getName());
		if (event.getFinalDamage() >= victim.getPlayer().getHealth()) {
			event.setCancelled(true);
			onDeath(event, victim, attacker);
		}
	}

	private void onDeath(EntityDamageEvent event, IMember victim, IMember attacker) {
		//todo call api events

		match.getGameSpectatorManager().spectate(victim);

		if (attacker == null) {
			match.broadcastInGame("player-dead.without-attacker", new PlaceholderUtil().
					add("{victim}", Utils.getDisplayName(victim.getPlayer())));
		} else {
			match.broadcastInGame("player-dead.by-attacker", new PlaceholderUtil()
					.add("{attacker-health}", attacker.getPlayer().getHealthScale() + "")
					.add("{attacker}", Utils.getDisplayName(attacker.getPlayer()))
					.add("{victim}", Utils.getDisplayName(victim.getPlayer())));
		}

		if (match.getGameDataStorage().getGameTeamManager().checkTeamEliminated(victim.getTeam())) {
			match.broadcastInGame("duel-team-eliminated", new PlaceholderUtil().add("{victim}", Utils.getDisplayName(victim.getPlayer())));
		}

		final ITeam lastSurvivedTeam = match.getGameDataStorage().getGameTeamManager().getLastSurvivedTeam();
		if (lastSurvivedTeam != null) {
			match.getGameDataStorage().getGameRoundData().addWin(lastSurvivedTeam);
			match.getGameDataStorage().getGameRoundData().setWinnerTeam(lastSurvivedTeam);

			next();
		}
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

			if (member.getTeam().getMembers().size() == 0 && match.getAllMembers().size() > 0) {
				Logger.debug(Logger.DebugPart.GAME, "Game finishing...");
				match.finish();
			}
		}

	}

	@Override
	public void leave(ISpectator player) {

	}

	@Override
	public Game getMatch() {
		return match;
	}

	@Override
	public GameStateType getGameStateType() {
		return GameStateType.PLAYING;
	}

}
