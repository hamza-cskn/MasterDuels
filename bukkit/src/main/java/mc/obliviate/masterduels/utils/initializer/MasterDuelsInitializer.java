package mc.obliviate.masterduels.utils.initializer;

import com.hakan.core.HCore;
import com.hakan.core.message.MessageHandler;
import com.hakan.core.packet.PacketHandler;
import com.hakan.core.scoreboard.ScoreboardHandler;
import mc.obliviate.inventory.InventoryAPI;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.arenaclear.ArenaClearListener;
import mc.obliviate.masterduels.arenaclear.modes.smart.SmartArenaClearHandler;
import mc.obliviate.masterduels.bossbar.BossBarHandler;
import mc.obliviate.masterduels.commands.DeveloperCMD;
import mc.obliviate.masterduels.commands.DuelAdminCMD;
import mc.obliviate.masterduels.commands.DuelCMD;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.listeners.*;
import mc.obliviate.masterduels.playerdata.history.HistoryListener;
import mc.obliviate.masterduels.scoreboard.InternalScoreboardManager;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.VaultUtil;
import mc.obliviate.masterduels.utils.advancedreplay.AdvancedReplayManager;
import mc.obliviate.masterduels.utils.metrics.Metrics;
import mc.obliviate.masterduels.utils.tab.TABManager;
import mc.obliviate.util.versiondetection.ServerVersionController;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class MasterDuelsInitializer {

    static {
        new Thread(() -> {
            try {
                new MasterDuelsInitializer().tryInit(60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void tryInit(int tryCount) throws InterruptedException {
        if (tryCount == 0) {
            Thread.currentThread().interrupt();
            return;
        }
        if (!Bukkit.getPluginManager().isPluginEnabled("MasterDuels")) {
            Thread.sleep(1000);
            tryInit(--tryCount);
        } else {
            init();
            Thread.currentThread().interrupt();
        }
    }

    public void init() {
        MasterDuels plugin = JavaPlugin.getPlugin(MasterDuels.class);
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getLogger().info("[MasterDuels] initialization process started. MasterDuels waking up."));
        final long now;
        try {
            final String out = new Scanner(new URL("http://worldtimeapi.org/api/ip").openStream(), String.valueOf(StandardCharsets.UTF_8)).useDelimiter("\\A").next();
            now = Long.parseLong(out.split(",")[11].split(":")[1]);
        } catch (Exception e) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getLogger().info("MasterDuels could not initialized. Exit code: 1");
                Bukkit.getLogger().info("Checkout the internet connection and firewall rules.");
            });
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        if (now < Long.parseLong("167035836405")) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (plugin.getDescription().getDescription().equalsIgnoreCase("-developerMode")) {
                    Logger.setDebugModeEnabled(true);
                }

                Bukkit.getLogger().info("MasterDuels development edition running on " + ServerVersionController.getServerVersion() + " - build v" + plugin.getDescription().getVersion());
                if (!checkObfuscated())
                    Bukkit.getLogger().info("This MasterDuels copy is not obfuscated.");

                // SETUP HANDLERS START
                plugin.getConfigurationHandler().prepare();
                new TABManager().init(plugin);
                new AdvancedReplayManager().init(plugin);
                plugin.getConfigurationHandler().init();
                new InventoryAPI(plugin).init();
                // SETUP ARENA CLEAR HANDLER START
                final String mode = ConfigurationHandler.getConfig().getString("arena-regeneration.mode", "SMART");
                if (!("ROLLBACKCORE".equals(mode) || "SLIMEWORLD".equals(mode) || "DISABLED".equals(mode))) {
                    plugin.setArenaClearHandler(new SmartArenaClearHandler(plugin));
                    Bukkit.getPluginManager().registerEvents(new ArenaClearListener(plugin), plugin);
                    plugin.getArenaClearHandler().init();
                }
                // SETUP ARENA CLEAR HANDLER END
                HCore.setInstance(plugin);
                PacketHandler.initialize();
                MessageHandler.initialize();
                ScoreboardHandler.initialize();

                if (ConfigurationHandler.getConfig().getBoolean("scoreboards.enabled", true))
                    new InternalScoreboardManager().init(plugin);
                if (ConfigurationHandler.getConfig().getBoolean("boss-bars.enabled"))
                    new BossBarHandler().init(plugin);
                plugin.getSqlManager().init();
                setupVaultUtils();
                Arrays.stream(GameRule.values()).forEach(GameRule::init);
                Logger.setDebugModeEnabled(ConfigurationHandler.getConfig().getBoolean("debug", false));
                // SETUP HANDLERS END

                //REGISTER LISTENERS START
                //RollbackListener registering from ArenaClear.java
                //Scoreboard and Boss bar listeners registering from their own manager classes
                Bukkit.getPluginManager().registerEvents(new ChatListener(), plugin);
                Bukkit.getPluginManager().registerEvents(new DuelProtectListener(), plugin);
                Bukkit.getPluginManager().registerEvents(new DamageListener(plugin), plugin);
                Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(plugin), plugin);
                Bukkit.getPluginManager().registerEvents(new SpectatorListener(), plugin);
                Bukkit.getPluginManager().registerEvents(new DeveloperCMD(plugin), plugin);
                Bukkit.getPluginManager().registerEvents(new CMDExecutorListener(), plugin);
                Bukkit.getPluginManager().registerEvents(new HistoryListener(plugin), plugin);
                //REGISTER LISTENERS END

                //REGISTER COMMANDS START
                safeRegisterCommand(plugin, "duel", new DuelCMD(plugin));
                safeRegisterCommand(plugin, "dueladmin", new DuelAdminCMD(plugin));
                //REGISTER COMMANDS END

                MasterDuels.setShutdownMode(false);
                startMetrics(plugin);
            });
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Logger.error("MasterDuels plugin license timed out.");
                Bukkit.getPluginManager().disablePlugin(plugin);
            });
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException ignore) {
            }
        }

    }

    public static boolean checkObfuscated() {
        try {
            String pack = "mc";
            pack = pack + ".obliviate";
            pack = pack + ".masterduels";
            pack = pack + ".utils.VaultUtil";
            Class.forName(pack);
            return false;
        } catch (ClassNotFoundException e) {
            return true;
        }
    }

    public static void setupVaultUtils() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) return;
        VaultUtil.vaultEnabled = true;

        if (!setupPermissions()) {
            Logger.warn("MasterDuels could not find Vault plugin. All permissions will be checked as OP permission.");
        }
        setupEconomy();

    }

    public static void safeRegisterCommand(MasterDuels plugin, String commandName, CommandExecutor executor) {
        final PluginCommand command = plugin.getCommand(commandName);
        if (command == null) {
            Logger.error("Command could not registered: " + commandName);
            return;
        }
        if (executor instanceof TabCompleter) {
            command.setTabCompleter((TabCompleter) executor);
        }
        command.setExecutor(executor);
    }

    public static void startMetrics(MasterDuels plugin) {
        new Metrics(plugin, 14587);
    }

    public static boolean setupEconomy() {
        final RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        MasterDuels.economy = rsp.getProvider();
        return MasterDuels.economy != null;
    }

    public static boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        MasterDuels.permissions = rsp.getProvider();
        return MasterDuels.permissions != null;
    }

}
