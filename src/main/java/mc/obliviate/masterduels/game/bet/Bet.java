package mc.obliviate.masterduels.game.bet;

import org.bukkit.entity.Player;

public class Bet {

	//todo players can bet for unlimited money. (even they doesn't have)
	//bets are dysfunctional
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
