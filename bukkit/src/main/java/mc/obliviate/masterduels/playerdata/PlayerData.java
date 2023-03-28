package mc.obliviate.masterduels.playerdata;

import org.bukkit.entity.*;

import java.io.Serializable;

public class PlayerData implements Serializable {

    private static final long serialVersionUID = 6523421098267757690L;

    private int hitClick;
    private int click;
    private int placedBlocks;
    private int brokenBlocks;
    private int damageTaken;
    private int damageDealt;
    private double regeneratedHealth;
    private int jump;
    private int walk;
    private int sprint;
    private int fall;

    private final ProjectileLogEntry<Arrow> arrow;
    private final ProjectileLogEntry<Snowball> snowball;
    private final ProjectileLogEntry<FishHook> fishHook;
    private final ProjectileLogEntry<Egg> egg;
    //todo TRIDENT and SPECTRAL ARROW support

    public PlayerData() {
        this.arrow = new ProjectileLogEntry<>();
        this.snowball = new ProjectileLogEntry<>();
        this.fishHook = new ProjectileLogEntry<>();
        this.egg = new ProjectileLogEntry<>();
    }

    public int getPlacedBlocks() {
        return placedBlocks;
    }

    public void setPlacedBlocks(int placedBlocks) {
        this.placedBlocks = placedBlocks;
    }

    public int getBrokenBlocks() {
        return brokenBlocks;
    }

    public void setBrokenBlocks(int brokenBlocks) {
        this.brokenBlocks = brokenBlocks;
    }

    public int getDamageTaken() {
        return damageTaken;
    }

    public void setDamageTaken(int damageTaken) {
        this.damageTaken = damageTaken;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(int damageDealt) {
        this.damageDealt = damageDealt;
    }

    public double getRegeneratedHealth() {
        return regeneratedHealth;
    }

    public void setRegeneratedHealth(double regeneratedHealth) {
        this.regeneratedHealth = regeneratedHealth;
    }

    public int getJump() {
        return jump;
    }

    public void setJump(int jump) {
        this.jump = jump;
    }

    public int getWalk() {
        return walk;
    }

    public void setWalk(int walk) {
        this.walk = walk;
    }

    public int getSprint() {
        return sprint;
    }

    public void setSprint(int sprint) {
        this.sprint = sprint;
    }

    public int getFall() {
        return fall;
    }

    public void setFall(int fall) {
        this.fall = fall;
    }

    public int getHitClick() {
        return hitClick;
    }

    public void setHitClick(int hitClick) {
        this.hitClick = hitClick;
    }

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public ProjectileLogEntry<Arrow> getArrow() {
        return arrow;
    }

    public ProjectileLogEntry<Snowball> getSnowball() {
        return snowball;
    }

    public ProjectileLogEntry<FishHook> getFishHook() {
        return fishHook;
    }

    public ProjectileLogEntry<Egg> getEgg() {
        return egg;
    }

    public ProjectileLogEntry getProjectileLog(Projectile projectile) {
        if (projectile instanceof Arrow) {
            return arrow;
        }
        if (projectile instanceof Snowball) {
            return snowball;
        }
        if (projectile instanceof Egg) {
            return egg;
        }
        if (projectile instanceof FishHook) {
            return fishHook;
        }
        return null;
    }

}
