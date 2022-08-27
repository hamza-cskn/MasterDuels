package mc.obliviate.masterduels.utils.tab;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.utils.Logger;
import org.bukkit.Bukkit;

public final class TABManager {

    private static Boolean enabled = null;

    public void init(MasterDuels plugin) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("TAB")) {
            TABManager.enabled = false;
            return;
        }
        try {
            Class.forName("me.neznamy.tab.api.TabAPI");
        } catch (ClassNotFoundException ignored) {
            Logger.error("TAB plugin found but its library could not.");
            TABManager.enabled = false;
            return;
        }

        TABManager.enabled = true;
        if (ConfigurationHandler.getConfig().getBoolean("tab-nametags.enabled"))
            new NameTagManager(plugin);

    }

    public static boolean isEnabled() {
        Preconditions.checkState(TABManager.enabled, "TabManager is not initialized.");
        return TABManager.enabled;
    }
}
