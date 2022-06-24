package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.arena.GameStateType;
import mc.obliviate.masterduels.api.arena.IGameState;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ISpectator;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.data.YamlStorageHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public interface GameState extends IGameState {

	void next();

	default void onDamage(EntityDamageEvent event, IMember victim, IMember attacker) {
		event.setCancelled(true);
	}

	default void onCommand(PlayerCommandPreprocessEvent event) {
		final IMember member = DataHandler.getMember(event.getPlayer().getUniqueId());
		if (member == null || event.getPlayer().isOp()) return;
		if (event.getMessage().startsWith("/")) {
			if (!YamlStorageHandler.getConfig().getStringList("executable-commands-by-player." + getGameStateType()).contains(event.getMessage())) {
				event.setCancelled(true);
				MessageUtils.sendMessage(event.getPlayer(), "command-is-blocked", new PlaceholderUtil().add("{command}", event.getMessage()));
			}
		}
	}

	void leave(IMember member);

	default void leave(ISpectator spectator) {
		getMatch().getGameSpectatorManager().unspectate(spectator);
	}

	default void join(Player player, IKit kit, int teamNo) {
		player.sendMessage("You cannot join to game in this state.");
	}

	GameStateType getGameStateType();

	Game getMatch();
}
