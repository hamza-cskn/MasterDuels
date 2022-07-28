package mc.obliviate.masterduels;

import com.hakan.core.HCore;
import com.hakan.core.message.HMessageHandler;
import com.hakan.core.packet.HPacketHandler;
import com.hakan.core.scoreboard.HScoreboardHandler;
import com.hakan.core.utils.ProtocolVersion;
import mc.obliviate.inventory.InventoryAPI;
import mc.obliviate.masterduels.arenaclear.ArenaClearListener;
import mc.obliviate.masterduels.arenaclear.IArenaClearHandler;
import mc.obliviate.masterduels.arenaclear.modes.smart.SmartArenaClearHandler;
import mc.obliviate.masterduels.bossbar.BossBarHandler;
import mc.obliviate.masterduels.commands.DeveloperCMD;
import mc.obliviate.masterduels.commands.DuelAdminCMD;
import mc.obliviate.masterduels.commands.DuelCMD;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.history.HistoryListener;
import mc.obliviate.masterduels.kit.serializer.KitSerializer;
import mc.obliviate.masterduels.listeners.*;
import mc.obliviate.masterduels.queue.DuelQueueHandler;
import mc.obliviate.masterduels.scoreboard.InternalScoreboardManager;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.advancedreplay.AdvancedReplayManager;
import mc.obliviate.masterduels.utils.metrics.Metrics;
import mc.obliviate.masterduels.utils.optimization.ArenaWorldOptimizerHandler;
import mc.obliviate.masterduels.utils.tab.TABManager;
import mc.obliviate.masterduels.utils.versioncontroller.ServerVersionController;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import static mc.obliviate.masterduels.utils.VaultUtil.vaultEnabled;

public class MasterDuels extends JavaPlugin {

	private static boolean shutdownMode = false;
	private static Economy economy;
	private static Permission permissions;
	private final ArenaWorldOptimizerHandler worldOptimizerHandler = new ArenaWorldOptimizerHandler();
	private final SQLManager sqlManager = new SQLManager(this);
	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private final ConfigurationHandler configurationHandler = new ConfigurationHandler(this);
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
		// Most of initialize methods has included to that method to make deobfuscation harder.
		getLogger().info("Loading process started.");
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			final long now;
			try {
				final String out = new Scanner(new URL("http://worldtimeapi.org/api/ip").openStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
				now = Long.parseLong(out.split(",")[11].split(":")[1]);
			} catch (Exception e) {
				getLogger().severe("MasterDuels could not initialized. Exit code: 77419929204588");
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}

			if (1659360775 > now) {
				Bukkit.getScheduler().runTask(this, () -> {
					if (getDescription().getDescription().equalsIgnoreCase("-developerMode")) {
						Logger.setDebugModeEnabled(true);
					}
					Bukkit.getLogger().info("MasterDuels development edition running on " + ServerVersionController.getServerVersion() + " - build " + getDescription().getVersion());
					if (!checkObfuscated())
						Bukkit.getLogger().info("This MasterDuels copy is not obfuscated.");

					// SETUP HANDLERS START

					configurationHandler.init();
					inventoryAPI.init();
					// LOAD KITS START
					final File file = new File(getDataFolder().getPath() + File.separator + "kits.yml");
					final YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
					for (final String key : data.getKeys(false)) {
						KitSerializer.deserialize(data.getConfigurationSection(key));
					}
					// LOAD KITS END
					new TABManager().init(this);
					new AdvancedReplayManager().init(this);
					// SETUP ARENA CLEAR HANDLER START
					final String mode = ConfigurationHandler.getConfig().getString("arena-regeneration.mode", "SMART");
					if (!("ROLLBACKCORE".equals(mode) || "SLIMEWORLD".equals(mode) || "DISABLED".equals(mode))) {
						arenaClearHandler = new SmartArenaClearHandler(this);
						Bukkit.getPluginManager().registerEvents(new ArenaClearListener(this), this);
						arenaClearHandler.init();
					}
					// SETUP ARENA CLEAR HANDLER END
					try {
						HCore.setInstance(this);
						Field field = HCore.class.getDeclaredField("VERSION");
						field.setAccessible(true);
						field.set(null, ProtocolVersion.getCurrentVersion());
						field.setAccessible(false);
						HPacketHandler.initialize();
						HMessageHandler.initialize();
						HScoreboardHandler.initialize();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

					if (ConfigurationHandler.getConfig().getBoolean("scoreboards.enabled", true))
						new InternalScoreboardManager().init(this);
					if (ConfigurationHandler.getQueues().getBoolean("duel-queues-enabled", true))
						duelQueueHandler.init();
					if (ConfigurationHandler.getConfig().getBoolean("optimize-duel-worlds", false))
						worldOptimizerHandler.init();
					if (ConfigurationHandler.getConfig().getBoolean("boss-bars.enabled"))
						new BossBarHandler().init(this);
					sqlManager.init();
					setupVaultUtils();
					Arrays.stream(GameRule.values()).forEach(GameRule::loadListener);
					Logger.setDebugModeEnabled(ConfigurationHandler.getConfig().getBoolean("debug", false));
					// SETUP HANDLERS END

					//REGISTER LISTENERS START
					//RollbackListener registering from ArenaClear.java
					//Scoreboard and Boss bar listeners registering from their own manager classes
					Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
					Bukkit.getPluginManager().registerEvents(new DuelProtectListener(), this);
					Bukkit.getPluginManager().registerEvents(new DamageListener(this), this);
					Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
					Bukkit.getPluginManager().registerEvents(new DeveloperCMD(this), this);
					Bukkit.getPluginManager().registerEvents(new CMDExecutorListener(), this);
					Bukkit.getPluginManager().registerEvents(new HistoryListener(this), this);
					//REGISTER LISTENERS END

					//REGISTER COMMANDS START
					safeRegisterCommand("duel", new DuelCMD(this));
					safeRegisterCommand("dueladmin", new DuelAdminCMD(this));
					//REGISTER COMMANDS END

					shutdownMode = false;
					startMetrics();
				});
			} else {
				Bukkit.getScheduler().runTask(this, () -> {
					Logger.error("MasterDuels plugin license timed out.");
					Bukkit.getPluginManager().disablePlugin(this);
				});
				try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException ignore) {
				}
			}
		});
	}

	private void startMetrics() {
		new Metrics(this, 14587);
	}

	private boolean checkObfuscated() {
		try {
			String pack = "mc";
			pack = pack + ".obliviate";
			pack = pack + ".masterduels";
			pack = pack + ".VaultUtil";
			Class.forName(pack);
			return false;
		} catch (ClassNotFoundException e) {
			return true;
		}
	}

	private void setupVaultUtils() {
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			vaultEnabled = true;
		}
		if (!setupPermissions()) {
			Logger.warn("MasterDuels could not find Vault plugin. All permissions will be checked as OP permission.");
		}
		if (!setupEconomy()) {
			Logger.warn("MasterDuels could not find Vault plugin. All players will authorized for economy activities.");
		}
	}

	private void safeRegisterCommand(String commandName, CommandExecutor executor) {
		final PluginCommand command = getCommand(commandName);
		if (command == null) {
			return;
		}
		command.setExecutor(executor);
	}

	@Override
	public void onDisable() {
		shutdownMode = true;
		for (final Match game : DataHandler.getArenas().values()) {
			if (game != null) {
				game.uninstall();
			}
		}
		getSqlManager().disconnect();
	}

	private boolean setupEconomy() {
		final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}

	private boolean setupPermissions() {
		final RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		permissions = rsp.getProvider();
		return permissions != null;
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
}
