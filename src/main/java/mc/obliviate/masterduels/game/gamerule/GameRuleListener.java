package mc.obliviate.masterduels.game.gamerule;

import mc.obliviate.masterduels.api.events.spectator.DuelGamePreSpectatorJoinEvent;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class GameRuleListener implements Listener {

	@EventHandler
	public void onIgnite(EntityCombustEvent e) {
		if (!e.getEntityType().equals(EntityType.PLAYER))
			return;

		final Player player = (Player) e.getEntity();
		final Member member = DataHandler.getMember(player.getUniqueId());
		if (member == null) return;
		if (!member.getGame().getGameRules().contains(GameRule.NO_FIRE)) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent e) {
		final Player player = e.getPlayer();
		final Member member = DataHandler.getMember(player.getUniqueId());
		if (member == null) return;
		if (e.getItem().getData().equals(XMaterial.ENCHANTED_GOLDEN_APPLE.parseItem().getData())) {
			if (!member.getGame().getGameRules().contains(GameRule.NO_ENCHANTED_GOLDEN_APPLE)) return;
		} else if (e.getItem().getData().equals(XMaterial.GOLDEN_APPLE.parseItem().getData())) {
			if (!member.getGame().getGameRules().contains(GameRule.NO_GOLDEN_APPLE)) return;
		} else if (e.getItem().getType().equals(XMaterial.POTION.parseMaterial())) {
			if (!member.getGame().getGameRules().contains(GameRule.NO_POTION)) return;
		}

		e.setCancelled(true);
	}

	@EventHandler
	public void onPotion(PotionSplashEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) return;
		final Player player = (Player) e.getEntity().getShooter();
		final Member member = DataHandler.getMember(player.getUniqueId());
		if (member == null) return;
		if (!member.getGame().getGameRules().contains(GameRule.NO_POTION)) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		final Player player = (Player) e.getEntity();
		final Member member = DataHandler.getMember(player.getUniqueId());
		if (member == null) return;
		if (!member.getGame().getGameRules().contains(GameRule.NO_BOW)) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onSpectate(DuelGamePreSpectatorJoinEvent e) {
		if (!e.getGame().getGameRules().contains(GameRule.NO_SPECTATOR)) return;
		e.setCancelled(true);
	}



}
