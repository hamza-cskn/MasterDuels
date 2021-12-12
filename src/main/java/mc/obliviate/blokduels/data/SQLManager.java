package mc.obliviate.blokduels.data;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.bloksqliteapi.sqlutils.DataType;
import mc.obliviate.bloksqliteapi.sqlutils.SQLTable;

public class SQLManager extends mc.obliviate.bloksqliteapi.SQLHandler {

	private final BlokDuels plugin;

	public SQLManager(BlokDuels plugin) {
		super(plugin.getDataFolder().getPath());
		this.plugin = plugin;
	}

	public void init() {
		connect("database");
	}

	@Override
	public void onConnect() {
		super.onConnect();

		final SQLTable table = new SQLTable("blokduels", "uuid");
		table.addField("uuid", DataType.TEXT)
				.addField("gameHistory", DataType.TEXT)
				.addField("wins", DataType.INTEGER)
				.addField("loses", DataType.INTEGER);
		table.create();

	}



}
