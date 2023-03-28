package mc.obliviate.masterduels.bossbar;

public class BossBarConfig {

    private final String playingTextFormat;
    private final String endingTextFormat;

    public BossBarConfig(String playingTextFormat, String endingTextFormat) {
        this.playingTextFormat = playingTextFormat;
        this.endingTextFormat = endingTextFormat;
    }

    public String getEndingTextFormat() {
        return endingTextFormat;
    }

    public String getPlayingTextFormat() {
        return playingTextFormat;
    }
}
