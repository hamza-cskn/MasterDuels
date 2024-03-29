package mc.obliviate.masterduels.queue;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchDataStorage;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.utils.Logger;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;
import java.util.*;

public class DuelQueueTemplate {

    private static ConfigurationSection section = null; //default values section
    private static final List<DuelQueueTemplate> queueTemplates = new LinkedList<>();
    private final String queueTemplateName;
    private final Kit kit;

    private final Set<DuelQueue> queues = new HashSet<>();

    /**
     * When allowed maps list is empty, all maps are valid for the queue.
     */
    private final List<String> allowedMaps;

    private DuelQueueTemplate(final String queueTemplateName, Kit kit, List<String> allowedMaps) {
        this.queueTemplateName = queueTemplateName;
        this.kit = kit;
        this.allowedMaps = allowedMaps;
        DuelQueueTemplate.queueTemplates.add(this);
        createNewQueue();
    }

    public static DuelQueueTemplate deserialize(final ConfigurationSection section) {
        DuelQueueTemplate.section = section;

        final String kitName = section.getString("kit", "DISABLED");
        Kit kit;
        if (kitName.equalsIgnoreCase("DISABLED")) {
            kit = null;
        } else {
            kit = Kit.getKits().get(kitName);
            if (kit == null) Logger.warn("Kit could not find(" + section.getName() + " queue): " + kitName);
        }

        final List<String> allowedMaps = section.getStringList("maps");
        if (allowedMaps.contains("*")) {
            allowedMaps.clear();
        }

        return new DuelQueueTemplate(section.getName(), kit, allowedMaps);
    }

    private static MatchDataStorage deserializeMatchDataStorage(final ConfigurationSection section) {
        Preconditions.checkNotNull(section, "Queue template section cannot be null!");

        final int teamAmount = section.getInt("team-amount", 2);
        final int teamSize = section.getInt("team-size", 1);
        final int duration = section.getInt("game-duration", 60);
        final int rounds = section.getInt("rounds", 1);

        final MatchDataStorage matchDataStorage = new MatchDataStorage();
        matchDataStorage.getGameTeamManager().setTeamsAttributes(teamSize, teamAmount);
        matchDataStorage.setMatchDuration(Duration.ofSeconds(duration));
        matchDataStorage.getGameRoundData().setTotalRounds(rounds);
        return matchDataStorage;
    }

    public static List<DuelQueueTemplate> getQueueTemplates() {
        return DuelQueueTemplate.queueTemplates;
    }

    public static boolean removeQueueTemplate(String name) {
        final DuelQueueTemplate template = getQueueTemplateFromName(name);
        return DuelQueueTemplate.queueTemplates.remove(template);
    }

    public static DuelQueueTemplate getQueueTemplateFromName(String name) {
        for (DuelQueueTemplate duelQueueTemplate : DuelQueueTemplate.queueTemplates) {
            if (duelQueueTemplate.getName().equalsIgnoreCase(name)) {
                return duelQueueTemplate;
            }
        }
        throw new IllegalArgumentException("queue template could not found: " + name);
    }

    public void createNewQueue() {
        this.registerQueue(new DuelQueue(this, Match.create(DuelQueueTemplate.deserializeMatchDataStorage(DuelQueueTemplate.section))));
    }

    public void unregisterQueue(DuelQueue queue) {
        queues.remove(queue);
    }

    public void registerQueue(DuelQueue queue) {
        queues.add(queue);
    }

    public Set<DuelQueue> getQueues() {
        return queues;
    }

    public Kit getKit() {
        return this.kit;
    }

    public List<String> getAllowedMaps() {
        return Collections.unmodifiableList(this.allowedMaps);
    }

    public String getName() {
        return this.queueTemplateName;
    }


}
