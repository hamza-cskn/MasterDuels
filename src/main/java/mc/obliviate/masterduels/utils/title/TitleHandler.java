package mc.obliviate.masterduels.utils.title;

import com.hakan.messageapi.bukkit.title.Title;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class TitleHandler {

	public final static Map<TitleType, Title> titles = new HashMap<>();

	private static Title deseralize(ConfigurationSection section) {
		return new Title(section.getString("title", ""), section.getString("subtitle", ""), section.getInt("stay", 20), section.getInt("fadein", 10), section.getInt("fadeout", 10));
	}

	public static void registerTitle(TitleType titleType, ConfigurationSection section) {
		titles.put(titleType, deseralize(section));
	}

	public static Title getTitle(TitleType type) {
		return getTitle(type, null);
	}

	public static Title getTitle(TitleType type, final PlaceholderUtil placeholderUtil) {
		final Title title = titles.get(type);
		if (title == null) return new Title("", "", 0, 0, 0);
		return format(new Title(title.getTitle(), title.getSubtitle(), title.getStay(), title.getFadeIn(), title.getFadeOut()), placeholderUtil);
	}

	public static Title format(final Title title, final PlaceholderUtil placeholderUtil) {
		if (placeholderUtil == null) return title;
		title.setTitle(MessageUtils.parseColor(MessageUtils.applyPlaceholders(title.getTitle(), placeholderUtil)));
		title.setSubtitle(MessageUtils.parseColor(MessageUtils.applyPlaceholders(title.getSubtitle(), placeholderUtil)));
		return title;
	}

	public enum TitleType {
		ROUND_STARTING,
		SPECTATOR_JOIN,
		SPECTATOR_LEAVE,
	}

}
