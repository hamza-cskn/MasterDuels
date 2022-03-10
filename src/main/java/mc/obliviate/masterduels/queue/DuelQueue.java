package mc.obliviate.masterduels.queue;

import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.utils.MessageUtils;
import org.bukkit.Bukkit;
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
		final DuelQueue queue = availableQueues.get(template);
		if (queue != null) {
			queue.lock();
			Bukkit.broadcastMessage("double queue creation found in same template.");
		}
		availableQueues.put(template, this);
	}

	public static Map<DuelQueueTemplate, DuelQueue> getAvailableQueues() {
		return availableQueues;
	}

	public GameBuilder getBuilder() {
		return builder;
	}

	public void addPlayer(final Player player) {
		if (!builder.addPlayer(player)) MessageUtils.sendMessage(player,"queue.player-could-not-added");
		if (builder.getPlayers().size() == builder.getTeamSize() * builder.getTeamAmount()) {
			start();
		}
	}

	public void removePlayer(final Player player) {
		builder.removePlayer(player);
	}

	public void start() {
		final Game game = builder.build();
		if (game == null) return;
		lock();
		game.startGame();
		Bukkit.broadcastMessage("game started");
		//todo announce and explain players why game is not started.
	}

	/**
	 * when a queue locked, any player cannot join/leave the queue.
	 * queue leaves from available duel queue list.
	 */
	public void lock() {
		availableQueues.remove(template);
		template.createNewQueue();
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