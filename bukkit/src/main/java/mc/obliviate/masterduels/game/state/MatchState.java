package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public interface MatchState {

	void next();

	default void onDamage(EntityDamageEvent event, Member victim, Member attacker) {
		event.setCancelled(true);
	}

	default void onCommand(PlayerCommandPreprocessEvent event) {
		final Member member = UserHandler.getMember(event.getPlayer().getUniqueId());
		if (member == null || event.getPlayer().isOp()) return;
		if (event.getMessage().startsWith("/")) {
			if (!ConfigurationHandler.getConfig().getStringList("executable-commands-by-player." + getMatchStateType()).contains(event.getMessage())) {
				event.setCancelled(true);
				MessageUtils.sendMessage(event.getPlayer(), "command-is-blocked", new PlaceholderUtil().add("{command}", event.getMessage()));
			}
		}
	}

	default void leave(Member member) {
		Bukkit.getPluginManager().callEvent(new DuelMatchMemberLeaveEvent(member));
	}

	default void leave(Spectator spectator) {
		getMatch().getGameSpectatorManager().unspectate(spectator);
	}

	default void rejoin(Player player, Kit kit, int teamNo) {
		player.sendMessage("You cannot join to game in this state.");
	}

	MatchStateType getMatchStateType();

	Match getMatch();
}
