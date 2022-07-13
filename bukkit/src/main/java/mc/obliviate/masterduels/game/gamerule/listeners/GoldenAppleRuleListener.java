package mc.obliviate.masterduels.game.gamerule.listeners;

import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GoldenAppleRuleListener implements Listener {

	private final List<ItemStack> goldenApples = new ArrayList<>();

	public GoldenAppleRuleListener() {
		goldenApples.add(XMaterial.GOLDEN_APPLE.parseItem());
		goldenApples.add(XMaterial.ENCHANTED_GOLDEN_APPLE.parseItem());
	}


	@EventHandler(ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent e) {
		final Player player = e.getPlayer();
		final Member member = UserHandler.getMember(player.getUniqueId());
		if (member == null) return;

		final List<GameRule> gameRules = member.getMatch().getGameDataStorage().getGameRules();
		if (!gameRules.contains(GameRule.NO_GOLDEN_APPLE)) return;

		if (!Utils.containsSimiliar(e.getItem(), goldenApples)) return;

		e.setCancelled(true);
	}


}
