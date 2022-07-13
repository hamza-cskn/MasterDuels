package mc.obliviate.masterduels.game.gamerule.listeners;

import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnderPearlRuleListener implements Listener {

	private final List<ItemStack> enderPearlItems = new ArrayList<>();

	public EnderPearlRuleListener() {
		enderPearlItems.add(XMaterial.ENDER_PEARL.parseItem());
	}

	@EventHandler(ignoreCancelled = true)
	public void onLaunch(PlayerInteractEvent e) {
		if (e.getItem() == null) return;
		final Member member = UserHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_ENDER_PEARL)) return;
		if (!Utils.containsSimiliar(e.getItem(), enderPearlItems)) return;

		e.setCancelled(true);
	}
}
