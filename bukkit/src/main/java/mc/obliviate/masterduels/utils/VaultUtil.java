package mc.obliviate.masterduels.utils;

import mc.obliviate.masterduels.MasterDuels;
import org.bukkit.entity.Player;

public class VaultUtil {

    public static boolean vaultEnabled = false;

	public static boolean checkPermission(Player player, String permission) {
		if (!vaultEnabled) return player.isOp();
		return MasterDuels.getPermissions().has(player, permission);
	}

	public static boolean checkMoney(Player player, double money) {
		if (!vaultEnabled) return false;
		return MasterDuels.getEconomy().has(player, money);
	}

	public static boolean removeMoney(Player player, double money) {
		if (!vaultEnabled) return true;
		return MasterDuels.getEconomy().withdrawPlayer(player, money).transactionSuccess();
	}

	public static boolean addMoney(Player player, double money) {
		if (!vaultEnabled) return true;
		return MasterDuels.getEconomy().depositPlayer(player, money).transactionSuccess();
	}

}
