package mc.obliviate.masterduels.utils.notify;

import com.hakan.core.HCore;
import com.hakan.core.message.title.Title;
import mc.obliviate.util.versiondetection.ServerVersionController;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class TitleNotify implements NotifyAction {

    private final Title title;

    public TitleNotify(String title, String subtitle, int fadein, int stay, int fadeout) {
        this.title = new Title(title, subtitle, fadein, stay, fadeout);
    }

    public TitleNotify(ConfigurationSection section) {
        this(ChatColor.translateAlternateColorCodes('&', section.getString("title", "")),
                ChatColor.translateAlternateColorCodes('&', section.getString("subtitle", "")),
                section.getInt("fadein", 0),
                section.getInt("stay", 20),
                section.getInt("fadeout", 0));
    }

    @Override
    public void run(Player player) {
        sendTitle(player, title);
    }

    public static void sendTitle(Player player, Title title) {
        if (ServerVersionController.isServerVersionAtLeast(ServerVersionController.V1_16)) {
            Class<?> clazz = player.getClass();
            try {
                clazz.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class)
                        .invoke(player, title.getTitle(), title.getSubtitle(), title.getFadeIn(), title.getStay(), title.getFadeOut());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        HCore.sendTitle(player, title);
    }
}
