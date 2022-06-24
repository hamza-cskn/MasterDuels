package mc.obliviate.masterduels.utils.notify;

import mc.obliviate.masterduels.utils.Logger;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotifyActionStack {

	private static final Map<Integer, NotifyActionStack> NOTIFY_ACTION_MAP = new HashMap<>();
	private final int remainingTime;
	private final List<NotifyAction> notifyActionList;

	public NotifyActionStack(int remainingTime, List<NotifyAction> notifyActionList) {
		this.remainingTime = remainingTime;
		this.notifyActionList = notifyActionList;
		NOTIFY_ACTION_MAP.put(remainingTime, this);
	}

	public static NotifyActionStack deserialize(ConfigurationSection section) {
		final int remainingTime = section.getInt("remaining-time", -1);
		if (remainingTime < 0) Logger.error("remaining time cannot be null: duel-game-lock section in config.yml");

		List<NotifyAction> resultList = new ArrayList<>();
		if (section.isConfigurationSection("title")) {
			resultList.add(new TitleNotify(section.getConfigurationSection("title")));
		}
		if (section.isSet("sound")) {
			resultList.add(new SoundNotify(Sound.valueOf(section.getString("sound"))));
		}
		if (section.isSet("chat")) {
			resultList.add(new ChatAction(section.getString("chat")));
		}
		if (section.isSet("action-bar")) {
			resultList.add(new ActionBarAction(section.getString("action-bar")));
		}

		return new NotifyActionStack(remainingTime, resultList);
	}

	public static NotifyActionStack getActionAt(long remainingTimeInMillis) {
		int time = Math.round(remainingTimeInMillis / 1000f);
		return NOTIFY_ACTION_MAP.get(time);
	}

	public void run(Player player) {
		for (final NotifyAction action : notifyActionList) {
			action.run(player);
		}
	}
}
