package mc.obliviate.masterduels.scoreboard;

import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface BaseScoreboard {

    boolean isExist();

    Optional<Player> getPlayerSafe();

    Player getPlayer();

    String getTitle();

    InternalScoreboard setTitle(String title);

    int getUpdateInterval();

    InternalScoreboard setUpdateInterval(int updateInterval);

    String getLine(int line);

    InternalScoreboard setLine(int line, String text);

    InternalScoreboard setLines(List<String> lines);

    InternalScoreboard setLines(String... lines);

    InternalScoreboard removeLine(int line);

    InternalScoreboard expire(int time, TimeUnit timeUnit);

    InternalScoreboard expire(Duration duration);

    InternalScoreboard expire(int ticks);

    InternalScoreboard show();

    InternalScoreboard update(Consumer<InternalScoreboard> consumer);

    InternalScoreboard delete();
}
