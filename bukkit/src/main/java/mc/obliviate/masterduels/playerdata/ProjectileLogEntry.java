package mc.obliviate.masterduels.playerdata;

import org.bukkit.entity.Projectile;

import java.io.Serializable;

public class ProjectileLogEntry<T extends Projectile> implements Serializable {

	private int hit;
	private int threw;

	public ProjectileLogEntry() {
		this.hit = 0;
		this.threw = 0;
	}

	public ProjectileLogEntry(int hit, int threw) {
		this.hit = hit;
		this.threw = threw;
	}

	public void increaseThrew(int increment) {
		threw = threw + increment;
	}

	public void increaseHit(int increment) {
		hit = hit + increment;
	}

	public void setThrew(int threw) {
		this.threw = threw;
	}

	public int getThrew() {
		return threw;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public int getHit() {
		return hit;
	}
}
