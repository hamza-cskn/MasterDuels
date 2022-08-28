package mc.obliviate.masterduels;

import mc.obliviate.inventory.InventoryAPI;
import mc.obliviate.masterduels.arenaclear.IArenaClearHandler;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.queue.DuelQueueHandler;
import mc.obliviate.masterduels.utils.optimization.ArenaWorldOptimizerHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class MasterDuels extends JavaPlugin {

    private static boolean shutdownMode = false;
    public static Economy economy;
    public static Permission permissions;
    private final ArenaWorldOptimizerHandler worldOptimizerHandler = new ArenaWorldOptimizerHandler();
    private final SQLManager sqlManager = new SQLManager(this);
    private final InventoryAPI inventoryAPI = new InventoryAPI(this);
    private final ConfigurationHandler configurationHandler = ConfigurationHandler.createInstance(this);
    private final DuelQueueHandler duelQueueHandler = new DuelQueueHandler(this);
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
        String[] nothing_to_see_here = new String[17];
        nothing_to_see_here[0] = "⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⣀⣠⣤⣶⣶⣶⣤⣄⣀⣀⠄⠄⠄⠄⠄";
        nothing_to_see_here[1] = "⠄⠄⠄⠄⠄⠄⠄⠄⣀⣤⣤⣶⣿⣿⣿⣿⣿⣿⣿⣟⢿⣿⣿⣿⣶⣤⡀⠄";
        nothing_to_see_here[2] = "⠄⠄⠄⠄⠄⠄⢀⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣜⠿⠿⣿⣿⣧⢓";
        nothing_to_see_here[3] = "⠄⠄⠄⠄⠄⡠⢛⣿⣿⣿⡟⣿⣿⣽⣋⠻⢻⣿⣿⣿⣿⡻⣧⡠⣭⣭⣿⡧";
        nothing_to_see_here[4] = "⠄⠄⠄⠄⠄⢠⣿⡟⣿⢻⠃⣻⣨⣻⠿⡀⣝⡿⣿⣿⣷⣜⣜⢿⣝⡿⡻⢔";
        nothing_to_see_here[5] = "⠄⠄⠄⠄⠄⢸⡟⣷⢿⢈⣚⣓⡡⣻⣿⣶⣬⣛⣓⣉⡻⢿⣎⠢⠻⣴⡾⠫";
        nothing_to_see_here[6] = "⠄⠄⠄⠄⠄⢸⠃⢹⡼⢸⣿⣿⣿⣦⣹⣿⣿⣿⠿⠿⠿⠷⣎⡼⠆⣿⠵⣫";
        nothing_to_see_here[7] = "⠄⠄⠄⠄⠄⠈⠄⠸⡟⡜⣩⡄⠄⣿⣿⣿⣿⣶⢀⢀⣿⣷⣿⣿⡐⡇⡄⣿";
        nothing_to_see_here[8] = "⠄⠄⠄⠄⠄⠄⠄⠄⠁⢶⢻⣧⣖⣿⣿⣿⣿⣿⣿⣿⣿⡏⣿⣇⡟⣇⣷⣿";
        nothing_to_see_here[9] = "⠄⠄⠄⠄⠄⠄⠄⠄⠄⢸⣆⣤⣽⣿⡿⠿⠿⣿⣿⣦⣴⡇⣿⢨⣾⣿⢹⢸";
        nothing_to_see_here[10] = "⠄⠄⠄⠄⠄⠄⠄⠄⠄⢸⣿⠊⡛⢿⣿⣿⣿⣿⡿⣫⢱⢺⡇⡏⣿⣿⣸⡼";
        nothing_to_see_here[11] = "⠄⠄⠄⠄⠄⠄⠄⠄⠄⢸⡿⠄⣿⣷⣾⡍⣭⣶⣿⣿⡌⣼⣹⢱⠹⣿⣇⣧";
        nothing_to_see_here[12] = "⠄⠄⠄⠄⠄⠄⠄⠄⠄⣼⠁⣤⣭⣭⡌⢁⣼⣿⣿⣿⢹⡇⣭⣤⣶⣤⡝⡼";
        nothing_to_see_here[13] = "⠄⣀⠤⡀⠄⠄⠄⠄⠄⡏⣈⡻⡿⠃⢀⣾⣿⣿⣿⡿⡼⠁⣿⣿⣿⡿⢷⢸";
        nothing_to_see_here[14] = "⢰⣷⡧⡢⠄⠄⠄⠄⠠⢠⡛⠿⠄⠠⠬⠿⣿⠭⠭⢱⣇⣀⣭⡅⠶⣾⣷⣶";
        nothing_to_see_here[15] = "⠈⢿⣿⣧⠄⠄⠄⠄⢀⡛⠿⠄⠄⠄⠄⢠⠃⠄⠄⡜⠄⠄⣤⢀⣶⣮⡍⣴";
        nothing_to_see_here[16] = "⠄⠈⣿⣿⡀⠄⠄⠄⢩⣝⠃⠄⠄⢀⡄⡎⠄⠄⠄⠇⠄⠄⠅⣴⣶⣶⠄⣶";
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

    public DuelQueueHandler getDuelQueueHandler() {
        return duelQueueHandler;
    }

    public ArenaWorldOptimizerHandler getWorldOptimizerHandler() {
        return worldOptimizerHandler;
    }

    public static void setEconomy(Economy economy) {
        MasterDuels.economy = economy;
    }

    public static void setPermissions(Permission permissions) {
        MasterDuels.permissions = permissions;
    }

    public static void setShutdownMode(boolean shutdownMode) {
        MasterDuels.shutdownMode = shutdownMode;
    }

    public void setArenaClearHandler(IArenaClearHandler arenaClearHandler) {
        this.arenaClearHandler = arenaClearHandler;
    }

    public InventoryAPI getInventoryAPI() {
        return inventoryAPI;
    }
}
