package mc.obliviate.masterduels.utils;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.playerdata.history.PlayerHistoryLog;
import mc.obliviate.masterduels.utils.xmaterial.XMaterial;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Utils {

	private static final Map<UUID, String> nickedNames = new HashMap<>();

	public static void setNick(UUID playerUniqueId, String nick) {
		nickedNames.put(playerUniqueId, nick);
	}

	public static void resetNick(UUID playerUniqueId) {
		nickedNames.remove(playerUniqueId);
	}

	public static final List<ItemStack> teamIcons = Arrays.asList(
			XMaterial.RED_STAINED_GLASS.parseItem(),
			XMaterial.BLUE_STAINED_GLASS.parseItem(),
			XMaterial.YELLOW_STAINED_GLASS.parseItem(),
			XMaterial.LIME_STAINED_GLASS.parseItem(),
			XMaterial.PURPLE_STAINED_GLASS.parseItem(),
			XMaterial.CYAN_STAINED_GLASS.parseItem(),
			XMaterial.ORANGE_STAINED_GLASS.parseItem(),
			XMaterial.PINK_STAINED_GLASS.parseItem(),
			XMaterial.LIGHT_BLUE_STAINED_GLASS.parseItem(),
			XMaterial.GREEN_STAINED_GLASS.parseItem());

	public static int getPercentage(final double total, final double progress) {
		try {
			return (int) (progress / (total / 100d));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static String getDisplayName(OfflinePlayer player) {
		return nickedNames.getOrDefault(player.getUniqueId(), player.getName());
	}

	public static String getDisplayName(Player player) {
		return nickedNames.getOrDefault(player.getUniqueId(), player.getDisplayName());
	}

	public static void teleportToLobby(final Player player) {
		if (DataHandler.getLobbyLocation() != null) {
			if (DataHandler.getLobbyLocation().getWorld() != null) {
				if (!player.teleport(DataHandler.getLobbyLocation())) {
					if (!MasterDuels.isInShutdownMode()) {
						player.kickPlayer("You could not teleported to lobby.\n" + DataHandler.getLobbyLocation());
						Logger.error("Player " + player.getName() + " could not teleported to lobby. MasterDuels kicked him.");
					}
				}
			}
		}
	}

	public static PlaceholderUtil getPlaceholders(PlayerHistoryLog log) {
		if (log == null) {
			return new PlaceholderUtil()
					.add("{kit}", "??")
					.add("{placed-blocks}", "??")
					.add("{broken-blocks}", "??")
					.add("{damage-dealt}", "??")
					.add("{damage-taken}", "??")
					.add("{jump}", "??")
					.add("{fall}", "??")
					.add("{sprint}", "??")
					.add("{click}", "??")
					.add("{hit-click}", "??")
					.add("{fish-hook-accuracy}", "??")
					.add("{melee-accuracy}", "??")
					.add("{bow-accuracy}", "??")
					.add("{regenerated-health}", "??");
		}
		return new PlaceholderUtil()
				.add("{kit}", log.getKitName() == null ? MessageUtils.parseColor(MessageUtils.getMessage("kit.none-kit-name")) : log.getKitName())
				.add("{placed-blocks}", log.getPlayerData().getPlacedBlocks() + "")
				.add("{broken-blocks}", log.getPlayerData().getBrokenBlocks() + "")
				.add("{damage-dealt}", (log.getPlayerData().getDamageDealt() / 5d) + "")
				.add("{damage-taken}", (log.getPlayerData().getDamageTaken() / 5d) + "")
				.add("{jump}", log.getPlayerData().getJump() + "")
				.add("{fall}", (log.getPlayerData().getFall() / 100d) + "")
				.add("{sprint}", (log.getPlayerData().getSprint() / 100d) + "")
				.add("{click}", log.getPlayerData().getClick() + "")
				.add("{hit-click}", log.getPlayerData().getHitClick() + "")
				.add("{fish-hook-accuracy}", MessageUtils.getPercentage(log.getPlayerData().getFishHook().getThrew(), log.getPlayerData().getFishHook().getHit()) + "")
				.add("{melee-accuracy}", MessageUtils.getPercentage(log.getPlayerData().getClick(), log.getPlayerData().getHitClick()) + "")
				.add("{bow-accuracy}", MessageUtils.getPercentage(log.getPlayerData().getArrow().getThrew(), log.getPlayerData().getArrow().getHit()) + "")
				.add("{regenerated-health}", log.getPlayerData().getRegeneratedHealth() + "");
	}

	public static boolean containsSimiliar(ItemStack item, Iterable<ItemStack> iterable) {
		for (ItemStack itemstack : iterable) {
			if (item.isSimilar(itemstack)) {
				return true;
			}
		}
		return false;
	}

	public static Class loadClass(String classPath) {
		try {
			return Class.forName(classPath);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
