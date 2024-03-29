package mc.obliviate.masterduels.arena.elements;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ArenaCuboid {

	/**
	 * Purpose of class,
	 * storing blocks as a cuboid
	 * <p>
	 * Thanks to Tristiisch74 for the <a href="https://www.spigotmc.org/threads/region-cuboid.329859/">code</a>.
	 */

	private final int xMin;
	private final int xMax;
	private final int yMin;
	private final int yMax;
	private final int zMin;
	private final int zMax;
	private final double xMinCentered;
	private final double xMaxCentered;
	private final double yMinCentered;
	private final double yMaxCentered;
	private final double zMinCentered;
	private final double zMaxCentered;
	private final World world;

	public ArenaCuboid(final Location point1, final Location point2) {
		this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
		this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
		this.yMin = Math.min(point1.getBlockY(), point2.getBlockY());
		this.yMax = Math.max(point1.getBlockY(), point2.getBlockY());
		this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
		this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
		this.world = point1.getWorld();
		this.xMinCentered = this.xMin + 0.5;
		this.xMaxCentered = this.xMax + 0.5;
		this.yMinCentered = this.yMin + 0.5;
		this.yMaxCentered = this.yMax + 0.5;
		this.zMinCentered = this.zMin + 0.5;
		this.zMaxCentered = this.zMax + 0.5;
	}

	public Iterator<Location> blockList() {
		final ArrayList<Location> bL = new ArrayList<>(this.getTotalBlockSize());
		for (int x = this.xMin; x <= this.xMax; ++x) {
			for (int y = this.yMin; y <= this.yMax; ++y) {
				for (int z = this.zMin; z <= this.zMax; ++z) {
					bL.add(new Location(world, x, y, z));
				}
			}
		}
		return bL.iterator();
	}

	public Location getCenter() {
		return new Location(this.world,
				(this.xMax - this.xMin) / 2f + this.xMin,
				(this.yMax - this.yMin) / 2f + this.yMin, (this.zMax - this.zMin) / 2f + this.zMin);
	}

	public double getDistance() {
		return this.getPoint1().distance(this.getPoint2());
	}

	public double getDistanceSquared() {
		return this.getPoint1().distanceSquared(this.getPoint2());
	}

	public int getHeight() {
		return this.yMax - this.yMin + 1;
	}

	public Location getPoint1() {
		return new Location(this.world, this.xMin, this.yMin, this.zMin);
	}

	public Location getPoint2() {
		return new Location(this.world, this.xMax, this.yMax, this.zMax);
	}

	public Location getRandomLocation() {
		final Random rand = new Random();
		final int x = rand.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
		final int y = rand.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
		final int z = rand.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;
		return new Location(this.world, x, y, z);
	}

	public int getTotalBlockSize() {
		return this.getHeight() * this.getXWidth() * this.getZWidth();
	}

	public int getXWidth() {
		return this.xMax - this.xMin + 1;
	}

	public int getZWidth() {
		return this.zMax - this.zMin + 1;
	}

	public boolean isIn(final Location loc) {
		return loc.getWorld() == this.world &&
				loc.getBlockX() >= this.xMin &&
				loc.getBlockX() <= this.xMax &&
				loc.getBlockY() >= this.yMin &&
				loc.getBlockY() <= this.yMax &&
				loc.getBlockZ() >= this.zMin &&
				loc.getBlockZ() <= this.zMax;
	}

	public boolean isIn(final Player player) {
		return this.isIn(player.getLocation());
	}

	public boolean isInWithMarge(final Location loc, final double marge) {
		return
				loc.getWorld() == this.world &&
						loc.getX() >= this.xMinCentered - marge &&
						loc.getX() <= this.xMaxCentered + marge &&
						loc.getY() >= this.yMinCentered - marge &&
						loc.getY() <= this.yMaxCentered + marge &&
						loc.getZ() >= this.zMinCentered - marge &&
						loc.getZ() <= this.zMaxCentered + marge;
	}

	public List<Chunk> getChunks() {
		final List<Chunk> chunks = new ArrayList<>();
		for (int x = this.xMin; x <= this.xMax; x += 16) {
			for (int z = this.zMin; z <= this.zMax; z += 16) {
				chunks.add(new Location(world, x, 100, z).getChunk());
			}
		}
		return chunks;
	}

	public int getxMin() {
		return xMin;
	}

	public int getxMax() {
		return xMax;
	}

	public int getyMin() {
		return yMin;
	}

	public int getyMax() {
		return yMax;
	}

	public int getzMin() {
		return zMin;
	}

	public int getzMax() {
		return zMax;
	}

	public World getWorld() {
		return world;
	}
}
