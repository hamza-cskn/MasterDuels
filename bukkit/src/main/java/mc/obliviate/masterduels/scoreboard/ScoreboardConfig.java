package mc.obliviate.masterduels.scoreboard;

import mc.obliviate.masterduels.game.MatchStateType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardConfig {

    private static ScoreboardConfig defaultConfig;
    private final int intervalInTicks;
    private final Map<MatchStateType, ScoreboardFormatConfig> scoreboardFormatConfigs = new HashMap<>();

    public ScoreboardConfig(int intervalInTicks) {
        this.intervalInTicks = Math.max(intervalInTicks, 1);
    }

    public static ScoreboardConfig getDefaultConfig() {
        return defaultConfig;
    }

    public static void setDefaultConfig(ScoreboardConfig defaultConfig) {
        ScoreboardConfig.defaultConfig = defaultConfig;
    }

    public void registerFormatConfig(MatchStateType matchStateType, String liveOpponentFormat, String deadOpponentFormat, String quitOpponentFormat, String title, List<String> lines) {
        ScoreboardFormatConfig formatConfig = new ScoreboardFormatConfig(liveOpponentFormat, deadOpponentFormat, quitOpponentFormat, title, lines);
        scoreboardFormatConfigs.put(matchStateType, formatConfig);
    }

    public ScoreboardFormatConfig getFormatConfig(MatchStateType matchStateType) {
        return scoreboardFormatConfigs.get(matchStateType);
    }

    public int getIntervalInTicks() {
        return intervalInTicks;
    }
}
