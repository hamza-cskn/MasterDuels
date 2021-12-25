package mc.obliviate.blokduels.arenaclear.workloads;

import com.google.common.collect.Queues;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;

public class WorkLoadThread implements Runnable {

	private static final int MAX_MS_PER_TICK = 5;
	private final Plugin plugin;
	private final ArrayDeque<IWorkLoad> workLoadDeque = Queues.newArrayDeque();

	public WorkLoadThread(Plugin plugin) {
		this.plugin = plugin;
	}

	public void addWorkLoad(IWorkLoad workLoad) {
		workLoadDeque.add(workLoad);
	}

	@Override
	public void run() {
		final long stopTime = System.currentTimeMillis() + MAX_MS_PER_TICK;
		while (next() && System.currentTimeMillis() <= stopTime) {
			workLoadDeque.poll().compute();
		}
		if (next()) {
			Bukkit.getScheduler().runTaskLater(plugin, this, 1);
		}

	}

	public ArrayDeque<IWorkLoad> getWorkLoadDeque() {
		return workLoadDeque;
	}

	public boolean next() {
		return !workLoadDeque.isEmpty();
	}
}
