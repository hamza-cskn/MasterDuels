package mc.obliviate.masterduels.game.gamerule.listeners;

import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShieldRuleListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent e) {
		if (e.getItem() == null) return;
		final Member member = UserHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_BOW)) return;
		if (!e.getItem().isSimilar(XMaterial.SHIELD.parseItem())) return;

		e.setCancelled(true);
	}
}
