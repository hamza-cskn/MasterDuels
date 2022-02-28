package mc.obliviate.masterduels.queue;

import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameBuilder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DuelQueue {

	private static final Map<DuelQueueTemplate, DuelQueue> availableQueues = new HashMap<>();
	private final DuelQueueTemplate template;
	private final GameBuilder builder;

	public DuelQueue(final DuelQueueTemplate template, final GameBuilder builder) {
		this.builder = builder;
		this.template = template;
		availableQueues.put(template, this);
	}

	public static Map<DuelQueueTemplate, DuelQueue> getAvailableQueues() {
		return availableQueues;
	}

	private GameBuilder getBuilder() {
		return builder;
	}

	public void addPlayer(final Player player) {
		if (builder.addPlayer(player) && builder.getPlayers().size() == builder.getTeamSize() * builder.getTeamAmount()) {
			start();
		}
	}

	public void removePlayer(final Player player) {
		builder.removePlayer(player);
	}

	public void start() {
		availableQueues.remove(template);
		final Game game = builder.getGame();
		if (game == null) return;
		game.startGame();
	}

	public String getName() {
		return template.getName();
	}

	public static DuelQueue findQueueOfPlayer(Player player) {
		for (DuelQueue queue : availableQueues.values()) {
			if (queue.builder.getPlayers().contains(player)) return queue;
		}
		return null;
	}
}