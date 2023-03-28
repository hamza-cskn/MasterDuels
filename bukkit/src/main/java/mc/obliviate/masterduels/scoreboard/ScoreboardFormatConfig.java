package mc.obliviate.masterduels.scoreboard;

import mc.obliviate.masterduels.utils.MessageUtils;

import java.util.List;

public class ScoreboardFormatConfig {

	private final String liveOpponentFormat;
	private final String deadOpponentFormat;
	private final String quitOpponentFormat;
	private final String title;
	private final List<String> lines;

	ScoreboardFormatConfig(String liveOpponentFormat, String deadOpponentFormat, String quitOpponentFormat, String title, List<String> lines) {
		this.liveOpponentFormat = liveOpponentFormat;
		this.deadOpponentFormat = deadOpponentFormat;
		this.quitOpponentFormat = quitOpponentFormat;
		this.title = MessageUtils.parseColor(title);
		this.lines = lines;
	}

	public String getQuitOpponentFormat() {
		return quitOpponentFormat;
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
