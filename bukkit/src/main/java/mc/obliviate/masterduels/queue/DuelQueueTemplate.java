package mc.obliviate.masterduels.queue;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.game.GameDataStorage;
import mc.obliviate.masterduels.kit.Kit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedList;
import java.util.List;

public class DuelQueueTemplate {

	private static final List<DuelQueueTemplate> queueTemplates = new LinkedList<>();
	private final String queueTemplateName;
	private final MasterDuels plugin;
	private final GameDataStorage gameDataStorage;

	public DuelQueueTemplate(final MasterDuels plugin, final String queueTemplateName, GameDataStorage gameDataStorage) {
		this.queueTemplateName = queueTemplateName;
		this.plugin = plugin;
		this.gameDataStorage = gameDataStorage;
		queueTemplates.add(this);
		createNewQueue();
	}

	public static DuelQueueTemplate deserialize(final MasterDuels plugin, final ConfigurationSection section) {
		if (section == null) throw new IllegalArgumentException("Queue template section cannot be null!");

		final String name = section.getName();
		final String kitName = section.getString("kit");
		Kit kit;
		if (kitName == null) {
			kit = null;
		} else {
			kit = Kit.getKits().get(kitName);
		}

		final int teamAmount = section.getInt("team-amount", 2);
		final int teamSize = section.getInt("team-size", 1);
		final int duration = section.getInt("game-duration", 60);
		final int rounds = section.getInt("rounds", 1);

		final GameDataStorage gameDataStorage = new GameDataStorage();
		gameDataStorage.setKit(kit);
		gameDataStorage.getGameTeamManager().setTeamAmount(teamAmount);
		gameDataStorage.getGameTeamManager().setTeamSize(teamSize);
		gameDataStorage.setMatchDuration(Duration.ofSeconds(duration));
		gameDataStorage.getGameRoundData().setTotalRounds(rounds);

		return new DuelQueueTemplate(plugin, name, gameDataStorage);
	}

	public static List<DuelQueueTemplate> getQueueTemplates() {
		return queueTemplates;
	}

	public static boolean removeQueueTemplate(String name) {
		final DuelQueueTemplate template = getQueueTemplateFromName(name);
		return queueTemplates.remove(template);
	}

	public static DuelQueueTemplate getQueueTemplateFromName(String name) {
		for (DuelQueueTemplate duelQueueTemplate : queueTemplates) {
			if (duelQueueTemplate.getName().equalsIgnoreCase(name)) {
				return duelQueueTemplate;
			}
		}
		throw new IllegalArgumentException("queue template could not found: " + name);
	}

	public void createNewQueue() {
		new DuelQueue(this, new GameBuilder(plugin));
	}

	public String getName() {
		return queueTemplateName;
	}


}
