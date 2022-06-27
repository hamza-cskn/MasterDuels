package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.api.user.IUser;
import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.statistics.DuelStatistic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelUser implements IUser {

	//player uuid, duel user
	private static final Map<UUID, DuelUser> DUEL_USER_MAP = new HashMap<>();
	private final Player player;
	private boolean inviteReceiving;
	private final DuelStatistic statistic;
	private MatchBuilder matchBuilder = null;

	public static DuelUser getDuelUser(UUID playerUniqueId) {
		return DUEL_USER_MAP.get(playerUniqueId);
	}

	public static void loadDuelUser(SQLManager sqlManager, Player player) {
		if (sqlManager.getPlayerDataTable().exist(player.getUniqueId())) {
			ResultSet rs = sqlManager.getPlayerDataTable().select(player.getUniqueId().toString());
			DuelStatistic statistic = SQLManager.deserializeStatistic(rs, false);
			try {
				boolean receivesInvites = rs.getBoolean("receivesInvites");
				Bukkit.getLogger().info("[MasterDuels] Player " + player.getName() + " duel user data found. Loading.");
				new DuelUser(player, receivesInvites, statistic);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		} else {
			Bukkit.getLogger().info("[MasterDuels] Player " + player.getName() + " duel user data could not find. New data creating.");
			new DuelUser(player, true, DuelStatistic.createDefaultInstance(player.getUniqueId()));
		}
	}

	private DuelUser(Player player, boolean inviteReceiving, DuelStatistic statistic) {
		this.player = player;
		this.inviteReceiving = inviteReceiving;
		this.statistic = statistic;
		DUEL_USER_MAP.put(player.getUniqueId(), this);
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
