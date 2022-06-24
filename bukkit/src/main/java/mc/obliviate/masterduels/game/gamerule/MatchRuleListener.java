package mc.obliviate.masterduels.game.gamerule;

import mc.obliviate.masterduels.api.arena.GameRule;
import mc.obliviate.masterduels.api.events.spectator.DuelMatchPreSpectatorJoinEvent;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;

public class MatchRuleListener implements Listener {

	@EventHandler
	public void onIgnite(EntityDamageEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER)) return;

		switch (e.getCause()) {
			case LAVA:
			case FIRE:
			case FIRE_TICK:
				break;
			default:
				return;
		}

		final Player player = (Player) e.getEntity();
		final IMember member = DataHandler.getMember(player.getUniqueId());
		if (member == null) return;
		if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_FIRE)) return;
		e.setCancelled(true);
		player.setFireTicks(0);
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent e) {
		final Player player = e.getPlayer();
		final IMember member = DataHandler.getMember(player.getUniqueId());
		if (member == null) return;
		if (e.getItem().getData().equals(XMaterial.ENCHANTED_GOLDEN_APPLE.parseItem().getData())) {
			if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_ENCHANTED_GOLDEN_APPLE))
				return;
		} else if (e.getItem().getData().equals(XMaterial.GOLDEN_APPLE.parseItem().getData())) {
			if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_GOLDEN_APPLE)) return;
		} else if (e.getItem().getType().equals(XMaterial.POTION.parseMaterial())) {
			if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_POTION)) return;
		}

		e.setCancelled(true);
	}

	@EventHandler
	public void onPotion(PlayerInteractEvent e) {
		if (!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))) return;
		if (e.getItem() == null || e.getItem().getItemMeta() == null) return;
		if (!(e.getItem().getItemMeta() instanceof PotionMeta)) return;

		final IMember member = DataHandler.getMember(e.getPlayer().getUniqueId());
		if (member == null) return;
		if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_POTION)) return;
		e.setCancelled(true);
		e.getPlayer().updateInventory();

	}

	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		final Player player = (Player) e.getEntity();
		final IMember member = DataHandler.getMember(player.getUniqueId());
		if (member == null) return;
		if (!member.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_BOW)) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onSpectate(DuelMatchPreSpectatorJoinEvent e) {
		if (!e.getMatch().getGameDataStorage().getGameRules().contains(GameRule.NO_SPECTATOR)) return;
		e.setCancelled(true);
	}


}
