package mc.obliviate.masterduels.kit;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.kit.serializer.KitSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Kit {

    public static boolean USE_PLAYER_INVENTORIES = false;
    private static final Map<String, Kit> KITS = new HashMap<>();
    private final PlayerInventoryFrame playerInventoryFrame;
    private final String kitName;
    private ItemStack icon;

    public Kit(String name, ItemStack[] contents, ItemStack[] armorContents) {
        this(name, contents, armorContents, new ItemStack(Material.DIAMOND_CHESTPLATE));
    }

    public Kit(String name, ItemStack[] contents, ItemStack[] armorContents, ItemStack icon) {
        this.playerInventoryFrame = new PlayerInventoryFrame(contents, armorContents);
        this.kitName = name;
        KITS.put(name, this);
        this.icon = icon;
    }

    public static Map<String, Kit> getKits() {
        return KITS;
    }


    public static void load(final Kit kit, final Player player) {
        if (USE_PLAYER_INVENTORIES) return;
        if (kit == null) return;
        PlayerInventoryFrame.loadInventoryFrame(player, kit.playerInventoryFrame);
    }

    public static void save(MasterDuels plugin, Kit kit) {
        KitSerializer.serialize(kit, ConfigurationHandler.getKits().createSection(kit.getKitName()));
        try {
            ConfigurationHandler.getKits().save(new File(plugin.getDataFolder().getPath() + File.separator + ConfigurationHandler.KITS_FILE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ItemStack[] getArmorContents() {
        return playerInventoryFrame.getArmorContents();
    }

    public ItemStack[] getContents() {
        return playerInventoryFrame.getContents();
    }

    public String getKitName() {
        return kitName;
    }

    public ItemStack getIcon() {
        if (icon == null || icon.getType().equals(Material.AIR)) return new ItemStack(Material.DIAMOND_CHESTPLATE);
        return icon.clone();
    }

    @Override
    public String toString() {
        return kitName;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }
}
