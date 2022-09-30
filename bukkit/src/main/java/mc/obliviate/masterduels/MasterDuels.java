package mc.obliviate.masterduels;

import mc.obliviate.masterduels.arenaclear.IArenaClearHandler;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.queue.DuelQueueHandler;
import mc.obliviate.masterduels.utils.optimization.ArenaWorldOptimizerHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MasterDuels extends JavaPlugin {

    private static boolean shutdownMode = false;
    public static Economy economy;
    public static Permission permissions;
    private ArenaWorldOptimizerHandler worldOptimizerHandler;
    private final SQLManager sqlManager = new SQLManager(this);
    private final ConfigurationHandler configurationHandler = ConfigurationHandler.createInstance(this);
    private DuelQueueHandler duelQueueHandler;
    private IArenaClearHandler arenaClearHandler;

    public static MasterDuels getInstance() {
        return JavaPlugin.getPlugin(MasterDuels.class);
    }

    public static boolean isInShutdownMode() {
        return shutdownMode;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static Permission getPermissions() {
        return permissions;
    }

    @Override
    public void onEnable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(
                    ChatColor.RED + "You kicked by MasterDuels.\n" +
                            "Do not connect to the server during loading process next time." +
                            "\n" +
                            "\n" +
                            "You can re-join right now." +
                            "\n" +
                            "\n" + ChatColor.DARK_GRAY + "If you reloaded MasterDuels using a" +
                            "\n" + "dynamic plugin loader, do not do it again.");
        }
        this.duelQueueHandler = new DuelQueueHandler(this);
        this.worldOptimizerHandler = new ArenaWorldOptimizerHandler();
    }

    @Override
    public void onDisable() {
        shutdownMode = true;
        for (final Match match : DataHandler.getArenas().values()) {
            if (match != null) {
                match.uninstall();
            }
        }
        getSqlManager().saveAllUsers();
        getSqlManager().disconnect();
    }

    public ConfigurationHandler getConfigurationHandler() {
        return configurationHandler;
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public IArenaClearHandler getArenaClearHandler() {
        return arenaClearHandler;
    }

    public static void setShutdownMode(boolean shutdownMode) {
        MasterDuels.shutdownMode = shutdownMode;
    }

    public void setArenaClearHandler(IArenaClearHandler arenaClearHandler) {
        this.arenaClearHandler = arenaClearHandler;
    }

    public ArenaWorldOptimizerHandler getWorldOptimizerHandler() {
        return worldOptimizerHandler;
    }

    public DuelQueueHandler getDuelQueueHandler() {
        return duelQueueHandler;
    }
}
