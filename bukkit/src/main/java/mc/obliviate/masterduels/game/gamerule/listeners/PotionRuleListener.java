package mc.obliviate.masterduels.game.gamerule.listeners;

import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;

public class PotionRuleListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onLaunch(PlayerInteractEvent e) {
		if (e.getItem() == null) return;
		final Member member = UserHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_POTION)) return;
		if (!(e.getItem().getItemMeta() instanceof PotionMeta)) return;

		e.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent e) {
		if (e.getItem() == null) return;
		final Member member = UserHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_POTION)) return;
		if (!(e.getItem().getItemMeta() instanceof PotionMeta)) return;

		e.setCancelled(true);
	}
}
