package mc.obliviate.masterduels.utils.notify;

import com.hakan.core.HCore;
import com.hakan.core.message.title.HTitle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class TitleNotify implements NotifyAction {

	private final HTitle title;

	public TitleNotify(String title, String subtitle, int fadein, int stay, int fadeout) {
		this.title = new HTitle(title, subtitle, fadein, stay, fadeout);
	}

	public TitleNotify(ConfigurationSection section) {
		this(section.getString("title"),
				section.getString("subtitle"),
				section.getInt("fadein"),
				section.getInt("stay"),
				section.getInt("fadeout"));
	}

	@Override
	public void run(Player player) {
		HCore.sendTitle(player, title);
	}
}
