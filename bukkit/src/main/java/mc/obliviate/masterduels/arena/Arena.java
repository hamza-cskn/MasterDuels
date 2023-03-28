package mc.obliviate.masterduels.arena;

import mc.obliviate.masterduels.arena.elements.ArenaCuboid;
import mc.obliviate.masterduels.arena.elements.Positions;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.serializer.SerializerUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mc.obliviate.masterduels.arena.BasicArenaState.*;

public class Arena {

    private static final Map<Arena, Match> ARENAS_MAP = new HashMap<>();

    private final String name;
    private final String mapName;
    private final ArenaCuboid arenaCuboid;
    private final Map<String, Positions> positions;
    private final int maxTeamSize;
    private final int maxTeamAmount;
    private final Location spectatorLocation;
    private boolean enabled;

    public Arena(String name, String mapName, ArenaCuboid arenaCuboid, Map<String, Positions> positions, int maxTeamSize, int maxTeamAmount, Location spectatorLocation) {
        this(name, mapName, arenaCuboid, positions, maxTeamSize, maxTeamAmount, spectatorLocation, true);
    }

    public Arena(String name, String mapName, ArenaCuboid arenaCuboid, Map<String, Positions> positions, int maxTeamSize, int maxTeamAmount, Location spectatorLocation, boolean enabled) {
        this.name = name;
        this.mapName = mapName;
        this.arenaCuboid = arenaCuboid;
        this.positions = positions;
        this.maxTeamSize = maxTeamSize;
        this.maxTeamAmount = maxTeamAmount;
        this.spectatorLocation = spectatorLocation;
        this.enabled = enabled;
        ARENAS_MAP.put(this, null);
    }

    public static Map<Arena, Match> getArenasMap() {
        return Collections.unmodifiableMap(ARENAS_MAP);
    }

    public static void unregisterAllArenas() {
        ARENAS_MAP.clear();
    }

    public static void unregisterArena(final Arena arena) {
        ARENAS_MAP.remove(arena);
    }

    public static boolean registerGame(final Arena arena, final Match game) {
        return ARENAS_MAP.putIfAbsent(arena, game) != game;
    }

    public static void unregisterGame(final Arena arena) {
        ARENAS_MAP.put(arena, null);
    }

    public static Arena getArenaFromName(String arenaName) {
        for (final Arena arena : ARENAS_MAP.keySet()) {
            if (arena != null && arena.getName() != null && arena.getName().equalsIgnoreCase(arenaName)) {
                return arena;
            }
        }
        return null;
    }

    public static Arena getArenaAt(Location loc) {
        for (final Arena arena : Arena.getArenasMap().keySet()) {
            if (arena.arenaCuboid.isIn(loc)) return arena;
        }
        return null;
    }

    public static Arena findAppropriateArena(int teamSize, int teamAmount, List<String> allowedMaps) {
        for (final Map.Entry<Arena, Match> entry : Arena.getArenasMap().entrySet()) {
            if (entry.getKey().getMaxTeamAmount() >= teamAmount && entry.getKey().getMaxTeamSize() >= teamSize) {
                if (!entry.getKey().isEnabled()) continue;
                if (!allowedMaps.isEmpty() && !allowedMaps.contains(entry.getKey().getMapName())) continue;
                if (entry.getValue() == null) return entry.getKey();
            }
        }
        return null;
    }

    public static BasicArenaState getBasicArenaState(Arena arena) {
        if (arena == null) return UNKNOWN;
        final Match match = Arena.getArenasMap().get(arena);


		if (match == null) {
			if (arena.isEnabled()) {
				return EMPTY;
			} else {
				return DISABLED;
			}
		}

		switch (match.getMatchState().getMatchStateType()) {
			case PLAYING:
				return PLAYING;
			case MATCH_ENDING:
			case UNINSTALLING:
				return ENDING;
			case ROUND_STARTING:
			case ROUND_ENDING:
			case MATCH_STARING:
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
			Logger.error("Cuboid pos1 or pos2 couldn't deserialized. Arena: " + name);
			return null;
		}
		if (cuboidPos1.getWorld() != cuboidPos2.getWorld()) {
			Logger.error("Cuboid pos1 and pos2 are in different world. Arena: " + name);
			return null;
		}

		final ArenaCuboid arenaCuboid = new ArenaCuboid(cuboidPos1, cuboidPos2);

		final ConfigurationSection positionsSection = section.getConfigurationSection("positions");

		if (positionsSection == null) {
			Logger.error("Arena could not deserialized. Position node is null!");
			return null;
		}

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

		Location spectatorLocation;
		if (section.isConfigurationSection("spectator-position")) {
			spectatorLocation = SerializerUtils.deserializeLocationYAML(section.getConfigurationSection("spectator-position"));
		} else {
			spectatorLocation = positions.get("spawn-team-1").getLocation(1);
		}
		return new Arena(name, mapName, arenaCuboid, positions, teamSize, teamAmount, spectatorLocation);
	}

	public ConfigurationSection serialize(ConfigurationSection section) {

		section.set("name", name);
		section.set("map-name", mapName);
		SerializerUtils.serializeLocationYAML(section.createSection("arena-cuboid.position-1"), arenaCuboid.getPoint1());
		SerializerUtils.serializeLocationYAML(section.createSection("arena-cuboid.position-2"), arenaCuboid.getPoint2());
        SerializerUtils.serializeLocationYAML(section.createSection("spectator-position"), spectatorLocation);
        for (final String key : positions.keySet()) {
            final Positions poses = positions.get(key);
            for (final int id : poses.getLocations().keySet()) {
                final ConfigurationSection locSection = section.createSection("positions." + key + "." + id);
                final Location loc = poses.getLocation(id);
                SerializerUtils.serializeLocationYAML(locSection, loc);
            }
        }

        section.set("team-size", maxTeamSize);
        section.set("team-amount", maxTeamAmount);

        return section;

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ArenaCuboid getArenaCuboid() {
        return arenaCuboid;
    }

    public Map<String, Positions> getPositions() {
        return positions;
    }

    public int getMaxTeamAmount() {
        return maxTeamAmount;
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public String getName() {
        return name;
    }

    public String getMapName() {
        return mapName;
    }

	public Location getSpectatorLocation() {
		return spectatorLocation;
	}
}
