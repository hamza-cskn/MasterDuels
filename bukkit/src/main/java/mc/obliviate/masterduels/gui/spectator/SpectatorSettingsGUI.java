package mc.obliviate.masterduels.gui.spectator;

import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class SpectatorSettingsGUI extends ConfigurableGui {

    public SpectatorSettingsGUI(Player player) {
        super(player, "spectator-settings-gui");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        putDysfunctionalIcons(Arrays.asList("night-vision-off", "night-vision-on"));

        putIcon("speed-1", e -> changeFlySpeed(0.1f, "I"));
        putIcon("speed-2", e -> changeFlySpeed(0.2f, "II"));
        putIcon("speed-3", e -> changeFlySpeed(0.3f, "III"));
        putIcon("speed-4", e -> changeFlySpeed(0.4f, "IV"));
        putIcon("speed-5", e -> changeFlySpeed(0.5f, "V"));

        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            putIcon("night-vision-on", e -> {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                player.closeInventory();
                MessageUtils.sendMessage(player, "spectator.settings.night-vision-off");
            });
        } else {
            putIcon("night-vision-off", e -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
                player.closeInventory();
                MessageUtils.sendMessage(player, "spectator.settings.night-vision-on");
            });
        }
    }

    private void changeFlySpeed(float speed, String levelString) {
        player.setFlySpeed(speed);
        player.closeInventory();
        MessageUtils.sendMessage(player, "spectator.settings.flying-speed-change", new PlaceholderUtil().add("{level}", levelString));
    }
}
