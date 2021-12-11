package mc.obliviate.blokduels.utils.playerreset;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerReset {

	private boolean health = true;
	private boolean foodLevel = true;
	private boolean fallDistance = true;
	private boolean allowFlight = true;
	private boolean flying = true;
	private boolean gamemode = true;
	private boolean exhaustion = true;
	private boolean flySpeed = true;
	private boolean walkSpeed = true;
	private boolean exp = true;
	private boolean level = true;
	private boolean inventory = true;
	private boolean title = true;
	private boolean potion = true;

	public void reset(Player player) {
		if (health)
			player.setHealth(player.getMaxHealth());
		if (foodLevel)
			player.setFoodLevel(20);
		if (fallDistance)
			player.setFallDistance(0f);
		if (allowFlight)
			player.setAllowFlight(false);
		if (flying)
			player.setFlying(false);
		if (gamemode)
			player.setGameMode(GameMode.SURVIVAL);
		if (exhaustion)
			player.setExhaustion(0f);
		if (flySpeed)
			player.setFlySpeed(0.1f);
		if (walkSpeed)
			player.setWalkSpeed(0.2f);
		if (exp)
			player.setExp(0f);
		if (level)
			player.setLevel(0);
		if (inventory) {
			player.getInventory().clear();
			player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
		}
		if (potion)
			for (PotionEffect effect : player.getActivePotionEffects()) {
				player.removePotionEffect(effect.getType());
			}
		if (title)
			player.resetTitle();
	}

	public PlayerReset excludeHealth() {
		this.health = false;
		return this;
	}

	public PlayerReset excludeFoodLevel() {
		this.foodLevel = false;
		return this;
	}

	public PlayerReset excludeFallDistance() {
		this.fallDistance = false;
		return this;
	}

	public PlayerReset excludeAllowFlight() {
		this.allowFlight = false;
		return this;
	}

	public PlayerReset excludeFlying() {
		this.flying = false;
		return this;
	}

	public PlayerReset excludeGamemode() {
		this.gamemode = false;
		return this;
	}

	public PlayerReset excludeExhaustion() {
		this.exhaustion = false;
		return this;
	}

	public PlayerReset excludeFlyspeed() {
		this.flySpeed = false;
		return this;
	}

	public PlayerReset excludeWalkspeed() {
		this.walkSpeed = false;
		return this;
	}

	public PlayerReset excludeExp() {
		this.exp = false;
		return this;
	}

	public PlayerReset excludeLevel() {
		this.level = false;
		return this;
	}

	public PlayerReset excludeInventory() {
		this.inventory = false;
		return this;
	}

	public PlayerReset excludeTitle() {
		this.title = false;
		return this;
	}

	public PlayerReset excludePotion() {
		this.potion = false;
		return this;
	}
}

