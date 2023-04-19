package mc.obliviate.masterduels.data.database.user;

import mc.obliviate.masterduels.data.database.statistics.StatisticsDatabase;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.User;
import mc.obliviate.util.database.sql.ColumnValues;
import mc.obliviate.util.database.sql.SQLDatabase;
import mc.obliviate.util.database.sql.SQLDriverProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultUserDatabase extends SQLDatabase<User> implements UserDatabase {

	public static final String TABLE_NAME = "users";
	public static final String USER_ID_COLUMN = "uuid";
	public static final String RECEIVES_INVITES_COLUMN = "receivesInvites";
	public static final String SHOW_SCOREBOARD_COLUMN = "showScoreboard";
	public static final String SHOW_BOSS_BAR_COLUMN = "showBossBar";

	private final StatisticsDatabase statisticsDatabase;

	public DefaultUserDatabase(SQLDriverProvider provider, StatisticsDatabase statisticsDatabase) {
		super(provider);
		this.statisticsDatabase = statisticsDatabase;
	}

	@Override
	public List<ColumnValues> serialize(User user) {
		ColumnValues result = new ColumnValues();
		result.add(USER_ID_COLUMN, user.getPlayer().getUniqueId());
		result.add(RECEIVES_INVITES_COLUMN, user.inviteReceiving() ? 1 : 0);
		result.add(SHOW_SCOREBOARD_COLUMN, user.showScoreboard() ? 1 : 0);
		result.add(SHOW_BOSS_BAR_COLUMN, user.showBossBar() ? 1 : 0);
		return Collections.singletonList(result);
	}

	@Override
	public User load(Object id) {
		User user = super.load(id);
		Player player = Bukkit.getPlayer((UUID) id);
		if (user == null) {
			return new User(player,
					true,
					true,
					true,
					DuelStatistic.createDefaultInstance(player.getUniqueId()));
		}
		return user;
	}

	@Override
	public User deserialize(ResultSet rs) throws SQLException {
		if (!rs.next()) return null;
		UUID uuid = UUID.fromString(rs.getString(USER_ID_COLUMN));
		DuelStatistic statistic = statisticsDatabase.loadStatistics(uuid).join(); //wait statistics to load user.

		boolean receivesInvites = rs.getBoolean(RECEIVES_INVITES_COLUMN);
		boolean showScoreboard = rs.getBoolean(SHOW_SCOREBOARD_COLUMN);
		boolean showBossBar = rs.getBoolean(SHOW_BOSS_BAR_COLUMN);
		return new User(Bukkit.getPlayer(uuid), receivesInvites, showScoreboard, showBossBar, statistic);
	}

	@Override
	public String getId(User user) {
		return user.getPlayer().getUniqueId().toString();
	}

	@Override
	public String getIdColumn() {
		return USER_ID_COLUMN;
	}

	@Override
	public String getTable() {
		return TABLE_NAME;
	}

	@Override
	public CompletableFuture<IUser> loadUser(UUID uuid) {
		return CompletableFuture.supplyAsync(() -> super.load(uuid));
	}

	@Override
	public CompletableFuture<Void> saveUser(IUser user) {
		return super.saveAsync((User) user);
	}
}
