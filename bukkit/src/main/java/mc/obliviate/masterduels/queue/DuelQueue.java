package mc.obliviate.masterduels.queue;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.DuelMatchEndEvent;
import mc.obliviate.masterduels.api.queue.DuelQueueJoinEvent;
import mc.obliviate.masterduels.api.queue.DuelQueueLeaveEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelQueue {

    private boolean locked = false;

    private static final Map<DuelQueueTemplate, DuelQueue> availableQueues = new HashMap<>();
    private final DuelQueueTemplate template;
    private final MatchBuilder builder;

    private Match match;

    DuelQueue(final DuelQueueTemplate template, final MatchBuilder builder) {
        this.builder = builder;
        this.template = template;
        final DuelQueue queue = availableQueues.get(template);
        if (queue != null) {
            queue.lock();
            throw new IllegalStateException("double queue creation found in same template: " + template.getName());
        }
        availableQueues.put(template, this);
    }

    public static Map<DuelQueueTemplate, DuelQueue> getAvailableQueues() {
        return availableQueues;
    }

    public static DuelQueue findQueueOfPlayer(Player player) { //fixme change that usage
        for (DuelQueue queue : availableQueues.values()) {
            if (queue.builder.getPlayers().contains(player.getUniqueId())) return queue;
        }
        return null;
    }

    public MatchBuilder getBuilder() {
        return builder;
    }

    public void addPlayer(final Player player) {
        final DuelQueueJoinEvent event = new DuelQueueJoinEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return; //api cancel

        System.out.println(template.getKit() + "");
        builder.addPlayer(player, template.getKit());
        if (builder.getPlayers().size() == builder.getTeamSize() * builder.getTeamAmount()) {
            start();
        }
    }

    public void removePlayer(final Player player) {
        Bukkit.getPluginManager().callEvent(new DuelQueueLeaveEvent(this, player));
        builder.removePlayer(player);
    }

    public void start() {
        this.match = builder.build(template.getAllowedMaps());
        if (this.match == null) {
            for (final UUID uuid : builder.getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;
                MessageUtils.sendMessage(player, "no-arena-found");
            }
            return;
        }
        lock();
        if (!this.match.start()) return;
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQueueFinish(DuelMatchEndEvent event) {
                if (event.getMatch().equals(match)) {
                    finish();
                    HandlerList.unregisterAll(this);
                }
            }
        }, MasterDuels.getInstance());
    }

    public void finish() {
        this.template.unregisterQueue(this);
    }

    /**
     * when a queue locked, any player cannot join/leave the queue.
     * queue leaves from available duel queue list.
     */
    public void lock() {
        locked = true;
        availableQueues.remove(template);
        template.createNewQueue();
    }

    public boolean isLocked() {
        return locked;
    }

    public String getName() {
        return template.getName();
    }

    public Match getMatch() {
        return match;
    }
}