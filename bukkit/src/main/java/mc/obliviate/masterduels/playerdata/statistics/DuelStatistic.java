package mc.obliviate.masterduels.playerdata.statistics;

import mc.obliviate.masterduels.playerdata.PlayerData;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.util.UUID;

public class DuelStatistic implements Serializable {

    private final transient UUID playerUniqueId;
    private int wins;
    private int losses;
    private final PlayerData playerData;

    public DuelStatistic(UUID playerUniqueId, PlayerData playerData) {
        this.playerUniqueId = playerUniqueId;
        this.playerData = playerData;
    }

    public static DuelStatistic createDefaultInstance(UUID playerUniqueId) {
        return new DuelStatistic(playerUniqueId, new PlayerData());
    }

    public void migrate(PlayerData playerData) {
        Bukkit.broadcastMessage(playerUniqueId + " " + playerData.getPlacedBlocks() + " added in" + this);
        this.playerData.setClick(this.playerData.getClick() + playerData.getClick());
        this.playerData.setBrokenBlocks(this.playerData.getBrokenBlocks() + playerData.getBrokenBlocks());
        this.playerData.setDamageDealt(this.playerData.getDamageDealt() + playerData.getDamageDealt());
        this.playerData.setFall(this.playerData.getFall() + playerData.getFall());
        this.playerData.setDamageTaken(this.playerData.getDamageTaken() + playerData.getDamageTaken());
        this.playerData.setJump(this.playerData.getJump() + playerData.getJump());
        this.playerData.setSprint(this.playerData.getSprint() + playerData.getSprint());
        this.playerData.setHitClick(this.playerData.getHitClick() + playerData.getHitClick());
        this.playerData.setWalk(this.playerData.getWalk() + playerData.getWalk());
        this.playerData.setPlacedBlocks(this.playerData.getPlacedBlocks() + playerData.getPlacedBlocks());
        this.playerData.setRegeneratedHealth(this.playerData.getRegeneratedHealth() + playerData.getRegeneratedHealth());
        this.playerData.getArrow().setHit(this.playerData.getArrow().getHit() + playerData.getArrow().getHit());
        this.playerData.getArrow().setThrew(this.playerData.getArrow().getThrew() + playerData.getArrow().getThrew());
        this.playerData.getEgg().setHit(this.playerData.getEgg().getHit() + playerData.getEgg().getHit());
        this.playerData.getEgg().setThrew(this.playerData.getEgg().getThrew() + playerData.getEgg().getThrew());
        this.playerData.getFishHook().setHit(this.playerData.getFishHook().getHit() + playerData.getFishHook().getHit());
        this.playerData.getFishHook().setThrew(this.playerData.getFishHook().getThrew() + playerData.getFishHook().getThrew());
        this.playerData.getSnowball().setHit(this.playerData.getSnowball().getHit() + playerData.getSnowball().getHit());
        this.playerData.getSnowball().setThrew(this.playerData.getSnowball().getThrew() + playerData.getSnowball().getThrew());
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
}
