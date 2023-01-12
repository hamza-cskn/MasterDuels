package mc.obliviate.masterduels.game.gamerule;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.game.gamerule.listeners.*;
import mc.obliviate.util.versiondetection.ServerVersionController;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

public enum GameRule implements Serializable {

	/*NO_FIRE(listener),
	NO_ENDER_PEARL(listener),
	NO_BOW(listener),
	NO_DEAD_DROP(listener),
	NO_POTION(listener),
	NO_SPECTATOR(listener),
	NO_SHIELD(ServerVersionController.V1_9, listener),
	NO_TOTEM_OF_UNDYING(ServerVersionController.V1_14, listener);*/
	NO_SHIELD(ServerVersionController.V1_9, ShieldRuleListener.class),
	NO_FIRE(BurnListener.class),
	NO_POTION(PotionRuleListener.class),
	NO_ENDER_PEARL(EnderPearlRuleListener.class),
	NO_BOW(BowRuleListener.class),
	NO_GOLDEN_APPLE(GoldenAppleRuleListener.class);

	final ServerVersionController version;
	final Class<? extends Listener> listener;

	GameRule(Class<? extends Listener> listener) {
		this(ServerVersionController.V1_8, listener);
	}

	GameRule(ServerVersionController version, Class<? extends Listener> listener) {
		this.version = version;
		this.listener = listener;
	}

	public void init() {
		if (!doesSupport()) return;
		try {
			Listener theListener = listener.getConstructor().newInstance();
			Bukkit.getPluginManager().registerEvents(theListener, MasterDuels.getInstance());
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				 NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public boolean doesSupport() {
		return ServerVersionController.isServerVersionAtLeast(version);
	}

	public ServerVersionController getVersion() {
		return version;
	}
}
