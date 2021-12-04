package mc.obliviate.blokduels.bukkit.setup;

import mc.obliviate.blokduels.bukkit.BlokDuels;
import mc.obliviate.blokduels.bukkit.arena.Arena;
import mc.obliviate.blokduels.bukkit.arena.elements.ArenaCuboid;
import mc.obliviate.blokduels.bukkit.arena.elements.Positions;
import mc.obliviate.blokduels.bukkit.data.DataHandler;
import mc.obliviate.blokduels.bukkit.setup.gui.ArenaSetupGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArenaSetup implements Listener {

	private static final Map<UUID, ArenaSetup> setuppers = new HashMap<>();
	private final BlokDuels plugin;

	private final Player player;
	private final PositionSelection posSelection = new PositionSelection();

	//Arena Datas
	private String arenaName = "Unknown";
	private String mapName = "Unknown";
	private ArenaCuboid arenaCuboid = null;
	private Map<String, Positions> positions = new HashMap<>();
	private int teamSize = 1;
	private int teamAmount = 2;


	public ArenaSetup(BlokDuels plugin, final Player player) {
		this.plugin = plugin;
		this.player = player;
		if (setuppers.containsKey(player.getUniqueId())) {
			throw new IllegalStateException(player.getName() + " already is in a Arena Setup mode.");
		}
		setuppers.put(player.getUniqueId(), this);
	}

	public static boolean isNameUnique(String checkName) {
		for (final Arena arena : DataHandler.getArenas().keySet()) {
			if (arena == null) continue;
			if (arena.getName() == null) continue;
			if (arena.getName().equalsIgnoreCase(checkName)) {
				return false;
			}
		}
		return true;
	}

	@EventHandler
	public void onPosSelect(final PlayerInteractEvent e) {
		if (!e.getPlayer().equals(player)) return;

		if (e.getItem() == null || !e.getItem().getType().equals(Material.BLAZE_ROD)) return;

		if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			e.setCancelled(true);
			if (posSelection.getPos1() != null && posSelection.getPos1().equals(e.getClickedBlock().getLocation()))
				return;
			posSelection.setPos1(e.getClickedBlock().getLocation());
			player.sendMessage("Position 1 has selected!");
			Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendBlockChange(e.getClickedBlock().getLocation(), Material.REDSTONE_BLOCK, (byte) 0), 2);
			return;
		}

		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			e.setCancelled(true);
			if (posSelection.getPos2() != null && posSelection.getPos2().equals(e.getClickedBlock().getLocation())) {
				return;
			}
			posSelection.setPos2(e.getClickedBlock().getLocation());
			player.sendMessage("Position 2 has selected!");
			Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendBlockChange(e.getClickedBlock().getLocation(), Material.REDSTONE_BLOCK, (byte) 0), 2);

		}
	}

	@EventHandler
	public void onPowderClick(final PlayerInteractEvent e) {
		if (!e.getPlayer().equals(player)) return;

		if (e.getItem() == null || !e.getItem().getType().equals(Material.BLAZE_POWDER)) return;

		new ArenaSetupGUI(player, this).open();
	}

	public Player getPlayer() {
		return player;
	}

	public PositionSelection getPosSelection() {
		return posSelection;
	}

	public String getArenaName() {
		return arenaName;
	}

	public void setArenaName(String arenaName) {
		this.arenaName = arenaName;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public ArenaCuboid getArenaCuboid() {
		return arenaCuboid;
	}

	public void setArenaCuboid(ArenaCuboid arenaCuboid) {
		this.arenaCuboid = arenaCuboid;
	}

	public Map<String, Positions> getPositions() {

		//put new positions() instead of null values
		int i;
		for (i = 1; teamAmount >= i; i++) {
			if (!positions.containsKey("spawn-team-" + i)) {
				positions.put("spawn-team-" + i, new Positions());
			}
		}

		//remove over positions
		if (positions.size() > teamAmount) {
			for (i = positions.size(); i > teamAmount; i--) {
				positions.remove("spawn-team-" + i);
			}
		}
		return positions;
	}

	public void setPositions(Map<String, Positions> positions) {
		this.positions = positions;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}

	public int getTeamAmount() {
		return teamAmount;
	}

	public void setTeamAmount(int teamAmount) {
		this.teamAmount = teamAmount;
	}

	public boolean canCompile() {
		return arenaName != null &&
				!arenaName.equalsIgnoreCase("Unknown") &&
				mapName != null &&
				!mapName.equalsIgnoreCase("Unknown") &&
				arenaCuboid != null &&
				positions != null &&
				positions.size() == teamAmount &&
				getPositionsAmount() == teamAmount * teamSize &&
				teamSize != 0 &&
				teamAmount != 0 && isNameUnique(arenaName);
	}

	public Arena compile() {
		if (canCompile()) {
			final Arena arena = new Arena(arenaName, mapName, arenaCuboid, positions, teamSize, teamAmount);
			plugin.getDatabaseHandler().saveArena(arena);
			destroy();
			return arena;
		}
		return null;
	}

	public int getPositionsAmount() {
		int size = 0;
		final Map<String, Positions> positionsMap = getPositions();
		for (int i = 1; getTeamAmount() >= i; i++) {
			final Positions positions = positionsMap.get("spawn-team-" + i);
			if (positions == null) {
				Bukkit.broadcastMessage("spawn-team-" + i + "... is null");
				continue;
			}
			if (positions.getLocations() == null) continue;
			size += positions.getLocations().size();
		}
		return size;
	}

	public void destroy() {
		PlayerInteractEvent.getHandlerList().unregister(this);
		setuppers.remove(player.getUniqueId());
	}
}
