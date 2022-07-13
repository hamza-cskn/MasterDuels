package mc.obliviate.masterduels.game.task;

import java.util.HashMap;
import java.util.Map;

public class MatchTaskManager {

	private final Map<String, MatchTask> tasks = new HashMap<>();

	private void registerTask(MatchTask task) {
		tasks.put(task.getTaskName(), task);
	}

	public void repeatTask(String taskName, Runnable mainRunnable, Runnable cancelRunnable, long delay, long period) {
		registerTask(new MatchTask(taskName, mainRunnable, cancelRunnable, delay, period));
	}

	public void repeatTask(String taskName, Runnable mainRunnable, Runnable cancelRunnable, long period) {
		repeatTask(taskName, mainRunnable, cancelRunnable, 0, period);
	}

	public void repeatTask(String taskName, Runnable mainRunnable, long period) {
		repeatTask(taskName, mainRunnable, () -> {
		}, 0, period);
	}

	public void delayedTask(String taskName, Runnable mainRunnable, Runnable cancelRunnable, long delay) {
		registerTask(new MatchTask(taskName, mainRunnable, cancelRunnable, delay));
	}

	public void delayedTask(String taskName, Runnable mainRunnable, long delay) {
		delayedTask(taskName, mainRunnable, () -> {
		}, delay);
	}

	public void cancelTask(String prefix) {
		for (Map.Entry<String, MatchTask> entry : tasks.entrySet()) {
			if (entry.getKey().startsWith(prefix)) {
				entry.getValue().cancel();
			}
		}
	}

	public void cancelTasks() {
		tasks.values().forEach(MatchTask::cancel);
	}


}
