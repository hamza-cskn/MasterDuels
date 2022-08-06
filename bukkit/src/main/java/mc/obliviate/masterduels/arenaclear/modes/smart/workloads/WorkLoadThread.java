package mc.obliviate.masterduels.arenaclear.modes.smart.workloads;

import com.google.common.collect.Queues;
import mc.obliviate.masterduels.MasterDuels;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;

public class WorkLoadThread implements Runnable {

	private static final int MAX_MS_PER_TICK = 5;
	private final MasterDuels plugin;
	private final ArrayDeque<IWorkLoad> workLoadDeque = Queues.newArrayDeque();

	public WorkLoadThread(MasterDuels plugin) {
		this.plugin = plugin;
	}

	public void addWorkLoad(IWorkLoad workLoad) {
		workLoadDeque.add(workLoad);
	}

	@Override
	public void run() {
		final long stopTime = System.currentTimeMillis() + MAX_MS_PER_TICK;
		while (next() && System.currentTimeMillis() <= stopTime) {
			workLoadDeque.poll().compute(plugin);
		}
		if (next()) {
			if (MasterDuels.isInShutdownMode()) {
				run();
			} else {
				Bukkit.getScheduler().runTaskLater(plugin, this, 1);
			}
		}
	}

	public ArrayDeque<IWorkLoad> getWorkLoadDeque() {
		return workLoadDeque;
	}

	public boolean next() {
		return !workLoadDeque.isEmpty();
	}
}
