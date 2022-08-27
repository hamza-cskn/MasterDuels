package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User implements IUser {

	//player uuid, duel user
	private final Player player;
	private boolean inviteReceiving;
	private final DuelStatistic statistic;
	private MatchBuilder matchBuilder = null;

	User(Player player, boolean inviteReceiving, DuelStatistic statistic) {
		this.player = player;
		this.inviteReceiving = inviteReceiving;
		this.statistic = statistic;
	}

    protected static User loadDuelUser(SQLManager sqlManager, Player player) {
        if (sqlManager.getPlayerDataTable().exist(player.getUniqueId())) {
            ResultSet resultSet = sqlManager.getPlayerDataTable().select(player.getUniqueId().toString());
            try {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                DuelStatistic statistic = sqlManager.getStatistic(uuid);
                boolean receivesInvites = resultSet.getBoolean("receivesInvites");
                boolean showScoreboard = resultSet.getBoolean("showScoreboard");
                boolean showBossBar = resultSet.getBoolean("showBossBar");
                return new User(Bukkit.getPlayer(uuid), receivesInvites, showScoreboard, showBossBar, statistic);
            } catch (SQLException e) {
                Logger.severe("User data of " + player.getName() + " could not loaded.");
                throw new RuntimeException(e);
            }
        }
        return new User(player, true, true, true, DuelStatistic.createDefaultInstance(player.getUniqueId()));

    }

	public boolean isInMatchBuilder() {
		return matchBuilder != null;
	}

	public MatchBuilder getMatchBuilder() {
		return matchBuilder;
	}

	public void exitMatchBuilder() {
		setMatchBuilder(null);
	}

	public void setMatchBuilder(MatchBuilder duelSpace) {
		this.matchBuilder = duelSpace;
	}

	public boolean inviteReceiving() {
		return inviteReceiving;
	}

	public void setInviteReceiving(boolean inviteReceiving) {
		this.inviteReceiving = inviteReceiving;
	}

	public DuelStatistic getStatistic() {
		return statistic;
	}

	@Override
	public Player getPlayer() {
		return player;
	}
}
