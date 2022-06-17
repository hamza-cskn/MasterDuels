package mc.obliviate.masterduels.utils.title;

import com.hakan.core.HCore;
import com.hakan.core.message.title.HTitle;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TitleHandler {

	public final static Map<TitleType, HTitle> titles = new HashMap<>();

	private static HTitle deseralize(ConfigurationSection section) {
		return new HTitle(section.getString("title", ""), section.getString("subtitle", ""), section.getInt("stay", 20), section.getInt("fadein", 10), section.getInt("fadeout", 10));
	}

	public static void registerTitle(TitleType titleType, ConfigurationSection section) {
		titles.put(titleType, deseralize(section));
	}

	private static HTitle getTitle(TitleType type) {
		return getTitle(type, null);
	}

	private static HTitle getTitle(TitleType type, final PlaceholderUtil placeholderUtil) {
		final HTitle title = titles.get(type);
		if (title == null) return new HTitle("", "", 0, 0, 0);
		return format(new HTitle(title.getTitle(), title.getSubtitle(), title.getStay(), title.getFadeIn(), title.getFadeOut()), placeholderUtil);
	}

	private static HTitle format(final HTitle title, final PlaceholderUtil placeholderUtil) {
		title.setTitle(MessageUtils.parseColor(MessageUtils.applyPlaceholders(title.getTitle(), placeholderUtil)));
		title.setSubtitle(MessageUtils.parseColor(MessageUtils.applyPlaceholders(title.getSubtitle(), placeholderUtil)));
		return title;
	}

	public static void sendTitle(Player player, TitleType titleType) {
		HCore.sendTitle(player, TitleHandler.getTitle(titleType));
	}

	public static void sendTitle(Player player, TitleType titleType, PlaceholderUtil placeholderUtil) {
		HCore.sendTitle(player, TitleHandler.getTitle(titleType, placeholderUtil));
	}

	public enum TitleType {
		ROUND_STARTING,
		ROUND_STARTED,
		SPECTATOR_JOIN,
		SPECTATOR_LEAVE,
	}

}
