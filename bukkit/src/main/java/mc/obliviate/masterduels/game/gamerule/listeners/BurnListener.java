package mc.obliviate.masterduels.game.gamerule.listeners;

import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class BurnListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onLaunch(EntityCombustEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		final Player player = (Player) e.getEntity();
		final Member member = UserHandler.getMember(player.getUniqueId());
		if (member == null) return;
		if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_FIRE)) return;

		e.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		final Player player = (Player) e.getEntity();
		final Member member = UserHandler.getMember(player.getUniqueId());
		if (member == null) return;
		if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_FIRE)) return;

		switch (e.getCause()) {
			case FIRE:
			case LAVA:
			case FIRE_TICK:
				e.setCancelled(true);
		}

	}
}
