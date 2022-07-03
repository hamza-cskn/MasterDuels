package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public interface MatchState {

	void next();

	default void onDamage(EntityDamageEvent event, Member victim, Member attacker) {
		event.setCancelled(true);
	}

	void leave(Member member);

	default void leave(Spectator spectator) {
		getMatch().getGameSpectatorManager().unspectate(spectator);
	}

	default void rejoin(Player player, Kit kit, int teamNo) {
		player.sendMessage("You cannot join to game in this state.");
	}

	MatchStateType getMatchStateType();

	Match getMatch();
}
