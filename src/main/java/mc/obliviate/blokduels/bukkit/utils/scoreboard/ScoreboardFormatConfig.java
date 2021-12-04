package mc.obliviate.blokduels.bukkit.utils.scoreboard;

import java.util.List;

public class ScoreboardFormatConfig {

	private final String liveOpponentFormat;
	private final String deadOpponentFormat;
	private final String title;
	private final List<String> lines;

	public ScoreboardFormatConfig(String liveOpponentFormat, String deadOpponentFormat, String title, List<String> lines) {
		this.liveOpponentFormat = liveOpponentFormat;
		this.deadOpponentFormat = deadOpponentFormat;
		this.title = title;
		this.lines = lines;
	}

	public String getLiveOpponentFormat() {
		return liveOpponentFormat;
	}

	public String getDeadOpponentFormat() {
		return deadOpponentFormat;
	}

	public List<String> getLines() {
		return lines;
	}

	public String getTitle() {
		return title;
	}
}
