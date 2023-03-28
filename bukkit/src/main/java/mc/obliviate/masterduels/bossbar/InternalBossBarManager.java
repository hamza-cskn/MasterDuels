package mc.obliviate.masterduels.bossbar;

import com.hakan.core.HCore;
import com.hakan.core.message.bossbar.BossBar;
import com.hakan.core.message.bossbar.meta.BarColor;
import com.hakan.core.message.bossbar.meta.BarStyle;
import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStateChangeEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchDataStorage;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class InternalBossBarManager implements Listener {

    private final Map<Match, BossBar> bossBarMap = new HashMap<>();

    public InternalBossBarManager(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDuelMatchStateChange(DuelMatchStateChangeEvent event) {
        if (event.getNewState().getMatchStateType().equals(MatchStateType.MATCH_STARING)) {
            BossBar bossBar = HCore.createBossBar(BossBarHandler.getDefaultConfig().getPlayingTextFormat(), BarColor.WHITE, BarStyle.SEGMENTED_10);
            bossBarMap.put(event.getMatch(), bossBar);
            initializeBossBarTimer(event.getMatch(), bossBar);
            for (Member member : event.getMatch().getGameDataStorage().getGameTeamManager().getAllMembers()) {
                if (member.showBossBar())
                    bossBar.addPlayer(member.getPlayer());
            }
        }
    }

    @EventHandler
    public void onDuelMatchLeave(DuelMatchMemberLeaveEvent event) {
        BossBar bar = bossBarMap.get(event.getMatch());
        if (bar == null) return;
        bar.removePlayer(event.getMember().getPlayer());
    }

    private void initializeBossBarTimer(Match match, BossBar bar) {
        match.getGameTaskManager().repeatTask("BOSSBAR", () -> {
            if (match.getMatchState().getMatchStateType().equals(MatchStateType.MATCH_ENDING)) {
                bar.setProgress((Utils.getPercentage(MatchDataStorage.getEndDelay().toMillis(), (match.getGameDataStorage().getFinishTime() - System.currentTimeMillis())) / 100d));
                bar.setTitle(BossBarHandler.getDefaultConfig().getEndingTextFormat().replace("{time}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())).replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())));
            } else {
                bar.setProgress((Utils.getPercentage(match.getGameDataStorage().getMatchDuration().toMillis(), (match.getGameDataStorage().getFinishTime() - System.currentTimeMillis())) / 100d));
                bar.setTitle(BossBarHandler.getDefaultConfig().getPlayingTextFormat().replace("{time}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())).replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(match.getGameDataStorage().getFinishTime())));
            }
        }, null, 0, 20);
    }

}
