package mc.obliviate.blokduels.game.gamerule;

import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.user.team.Member;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class GameRuleListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if (e.getItem() == null) return;
		if (!e.getItem().getType().equals(Material.FLINT_AND_STEEL)) return;

		final Member member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		if (!member.getGame().getGameRules().contains(GameRule.NO_FIRE)) return;
		e.setCancelled(true);

	}

}
