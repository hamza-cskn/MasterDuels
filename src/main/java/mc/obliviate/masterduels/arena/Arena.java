package mc.obliviate.masterduels.arena;

import mc.obliviate.masterduels.arena.elements.ArenaCuboid;
import mc.obliviate.masterduels.arena.elements.Positions;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

import static mc.obliviate.masterduels.arena.BasicArenaState.*;

public class Arena {

	private final String name;
	private final String mapName;
	private final ArenaCuboid arenaCuboid;
	private final Map<String, Positions> positions;
	private final int teamSize;
	private final int teamAmount;
	private boolean enabled;

	public Arena(String name, String mapName, ArenaCuboid arenaCuboid, Map<String, Positions> positions, int teamSize, int teamAmount) {
		this(name, mapName, arenaCuboid, positions, teamSize, teamAmount, true);
	}

	public Arena(String name, String mapName, ArenaCuboid arenaCuboid, Map<String, Positions> positions, int teamSize, int teamAmount, boolean enabled) {
		this.name = name;
		this.mapName = mapName;
		this.arenaCuboid = arenaCuboid;
		this.positions = positions;
		this.teamSize = teamSize;
		this.teamAmount = teamAmount;
		this.enabled = enabled;
		DataHandler.registerArena(this);
	}

	public static Arena getArenaAt(Location loc) {
		for (final Arena arena : DataHandler.getArenas().keySet()) {
			if (arena.arenaCuboid.isIn(loc)) return arena;
		}
		return null;
	}

	public static Arena findArena(int teamSize, int teamAmount) {
		for (final Map.Entry<Arena, Game> entry : DataHandler.getArenas().entrySet()) {
			if (entry.getKey().getTeamAmount() >= teamAmount && entry.getKey().getTeamSize() >= teamSize) {
				if (!entry.getKey().isEnabled()) continue;
				if (entry.getValue() == null) return entry.getKey();
			}
		}
		return null;
	}

	public static BasicArenaState getBasicArenaState(Arena arena) {
		if (arena == null) return UNKNOWN;
		final Game game = DataHandler.getArenas().get(arena);


		if (game == null) {
			if (arena.isEnabled()) {
				return EMPTY;
			} else {
				return DISABLED;
			}
		}

		switch (game.getGameState()) {
			case BATTLE:
				return PLAYING;
			case GAME_ENDING:
			case UNINSTALLING:
				return ENDING;
			case ROUND_STARTING:
			case ROUND_ENDING:
			case GAME_STARING:
				return STARTING;
		}
		return UNKNOWN;

	}

	public static Arena deserialize(ConfigurationSection section) {
		final String name = section.getString("name");
		final String mapName = section.getString("map-name");

		final Location cuboidPos1 = SerializerUtils.deserializeLocationYAML(section.getConfigurationSection("arena-cuboid.position-1"));
		final Location cuboidPos2 = SerializerUtils.deserializeLocationYAML(section.getConfigurationSection("arena-cuboid.position-2"));

		if (cuboidPos1 == null || cuboidPos2 == null) {
			Bukkit.getLogger().severe("Cuboid pos1 or pos2 couldn't deserialized. Arena: " + name);
			return null;
		}
		if (cuboidPos1.getWorld() != cuboidPos2.getWorld()) {
			Bukkit.getLogger().severe("Cuboid pos1 and pos2 are in different world. Arena: " + name);
			return null;
		}

		final ArenaCuboid arenaCuboid = new ArenaCuboid(cuboidPos1, cuboidPos2);

		final ConfigurationSection positionsSection = section.getConfigurationSection("positions");

		final Map<String, Positions> positions = new HashMap<>();
		for (final String key : positionsSection.getKeys(false)) {
			final Positions poses = new Positions();
			positions.put(key, poses);
			final ConfigurationSection positionSection = positionsSection.getConfigurationSection(key);
			for (final String idString : positionSection.getKeys(false)) {
				final Location location = SerializerUtils.deserializeLocationYAML(positionSection.getConfigurationSection(idString));
				poses.registerLocation(Integer.parseInt(idString), location);
			}
		}

		final int teamSize = section.getInt("team-size");
		final int teamAmount = section.getInt("team-amount");

		return new Arena(name, mapName, arenaCuboid, positions, teamSize, teamAmount);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public ConfigurationSection serialize(ConfigurationSection section) {

		section.set("name", name);
		section.set("map-name", mapName);
		SerializerUtils.serializeLocationYAML(section.createSection("arena-cuboid.position-1"), arenaCuboid.getPoint1());
		SerializerUtils.serializeLocationYAML(section.createSection("arena-cuboid.position-2"), arenaCuboid.getPoint2());
		for (final String key : positions.keySet()) {
			final Positions poses = positions.get(key);
			for (final int id : poses.getLocations().keySet()) {
				final ConfigurationSection locSection = section.createSection("positions." + key + "." + id);
				final Location loc = poses.getLocation(id);
				SerializerUtils.serializeLocationYAML(locSection, loc);
			}
		}

		section.set("team-size", teamSize);
		section.set("team-amount", teamAmount);

		return section;

	}


	public ArenaCuboid getArenaCuboid() {
		return arenaCuboid;
	}

	public Map<String, Positions> getPositions() {
		return positions;
	}

	public int getTeamAmount() {
		return teamAmount;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public String getName() {
		return name;
	}

	public String getMapName() {
		return mapName;
	}
}
