package mc.obliviate.masterduels.data.database;

import com.hakan.core.HCore;
import mc.obliviate.masterduels.playerdata.history.MatchHistoryLog;
import mc.obliviate.util.database.sql.ColumnValues;
import mc.obliviate.util.database.sql.SQLDatabase;
import mc.obliviate.util.database.sql.SQLDriverProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class HistoryDatabase extends SQLDatabase<MatchHistoryLog> {

	public static final String TABLE_NAME = "history";
	public static final String HISTORY_ID_COLUMN = "uuid";
	public static final String STATISTIC_COLUMN = "statistic";

	protected HistoryDatabase(SQLDriverProvider provider) {
		super(provider);
	}

	@Override
	public List<ColumnValues> serialize(MatchHistoryLog matchHistoryLog) {
		return null;
	}

	@Override
	public MatchHistoryLog deserialize(ResultSet rs) throws SQLException {
		return HCore.deserialize(rs.getString("log"), MatchHistoryLog.class);
	}

	@Override
	public String getId(MatchHistoryLog matchHistoryLog) {
		return matchHistoryLog.getUniqueId().toString();
	}

	@Override
	public String getIdColumn() {
		return HISTORY_ID_COLUMN;
	}

	@Override
	public String getTable() {
		return TABLE_NAME;
	}
}
