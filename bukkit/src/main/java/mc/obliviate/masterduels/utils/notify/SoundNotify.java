package mc.obliviate.masterduels.utils.notify;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundNotify implements NotifyAction {

	private final Sound sound;

	public SoundNotify(Sound sound) {
		this.sound = sound;
	}

	@Override
	public void run(Player player) {
		player.playSound(player.getLocation(), sound, 1f, 1f);
	}
}
