package mc.obliviate.masterduels.utils.scoreboard;

import mc.obliviate.masterduels.api.arena.MatchStateType;
import mc.obliviate.masterduels.utils.MessageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardFormatConfig {

	private static final Map<MatchStateType, ScoreboardFormatConfig> SCOREBOARD_FORMAT_CONFIG_MAP = new HashMap<>();

	private final String liveOpponentFormat;
	private final String deadOpponentFormat;
	private final String title;
	private final List<String> lines;

	public ScoreboardFormatConfig(MatchStateType matchStateType, String liveOpponentFormat, String deadOpponentFormat, String quitOpponentFormat, String title, List<String> lines) {
		this.liveOpponentFormat = MessageUtils.parseColor(liveOpponentFormat);
		this.deadOpponentFormat = MessageUtils.parseColor(deadOpponentFormat);
		this.quitOpponentFormat = MessageUtils.parseColor(quitOpponentFormat);
		this.title = MessageUtils.parseColor(title);
		this.lines = MessageUtils.parseColor(lines);

		SCOREBOARD_FORMAT_CONFIG_MAP.put(matchStateType, this);
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

	public static ScoreboardFormatConfig getFormatConfig(MatchStateType matchStateType) {
		return SCOREBOARD_FORMAT_CONFIG_MAP.get(matchStateType);
	}
}
