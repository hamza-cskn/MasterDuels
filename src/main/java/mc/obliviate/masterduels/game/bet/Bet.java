package mc.obliviate.masterduels.game.bet;

import org.bukkit.entity.Player;

public class Bet {

	public static boolean betsEnabled = true;

	int money = 0;

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public void remove(Player player) {
		if (!betsEnabled) return;
	}

	public void delivery(Player player) {
		if (!betsEnabled) return;
	}
}
