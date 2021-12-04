package mc.obliviate.blokduels.bukkit.arena;

import mc.obliviate.blokduels.bukkit.arena.elements.ArenaCuboid;
import mc.obliviate.blokduels.bukkit.arena.elements.Positions;
import mc.obliviate.blokduels.bukkit.data.DataHandler;
import mc.obliviate.blokduels.bukkit.game.Game;
import mc.obliviate.blokduels.bukkit.utils.serializer.SerializerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class Arena {

	private final String name;
	private final String mapName;
	private final ArenaCuboid arenaCuboid;
	private final Map<String, Positions> positions;
	private final int teamSize;
	private final int teamAmount;

	public Arena(String name, String mapName, ArenaCuboid arenaCuboid, Map<String, Positions> positions, int teamSize, int teamAmount) {
		this.name = name;
		this.mapName = mapName;
		this.arenaCuboid = arenaCuboid;
		this.positions = positions;
		this.teamSize = teamSize;
		this.teamAmount = teamAmount;
		DataHandler.registerArena(this);
	}

	public static Arena findArena(int teamSize, int teamAmount) {
		for (final Map.Entry<Arena, Game> entry : DataHandler.getArenas().entrySet()) {
			if (entry.getKey().getTeamAmount() == teamAmount && entry.getKey().getTeamSize() == teamSize) {
				if (entry.getValue() == null) return entry.getKey();
			}
		}
		return null;
	}



	public static Arena deserialize(ConfigurationSection section) {
		final String name = section.getString("name");
		final String mapName = section.getString("map-name");

		final Location cuboidPos1 = SerializerUtils.deserializeLocation(section.getConfigurationSection("arena-cuboid.position-1"));
		final Location cuboidPos2 = SerializerUtils.deserializeLocation(section.getConfigurationSection("arena-cuboid.position-2"));

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
				final Location location = SerializerUtils.deserializeLocation(positionSection.getConfigurationSection(idString));
				poses.registerLocation(Integer.parseInt(idString), location);
			}
		}

		final int teamSize = section.getInt("team-size");
		final int teamAmount = section.getInt("team-amount");

		return new Arena(name, mapName, arenaCuboid, positions, teamSize, teamAmount);
	}

	public ConfigurationSection serialize(ConfigurationSection section) {

		section.set("name", name);
		section.set("map-name", mapName);
		SerializerUtils.serializeLocation(section.createSection("arena-cuboid.position-1"), arenaCuboid.getPoint1());
		SerializerUtils.serializeLocation(section.createSection("arena-cuboid.position-2"), arenaCuboid.getPoint2());
		for (final String key : positions.keySet()) {
			final Positions poses = positions.get(key);
			for (final int id : poses.getLocations().keySet()) {
				final ConfigurationSection locSection = section.createSection("positions." + key + "." + id);
				final Location loc = poses.getLocation(id);
				SerializerUtils.serializeLocation(locSection, loc);
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
