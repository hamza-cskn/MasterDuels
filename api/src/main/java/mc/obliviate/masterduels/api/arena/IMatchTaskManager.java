package mc.obliviate.masterduels.api.arena;

public interface IMatchTaskManager {

	void repeatTask(String taskName, Runnable mainRunnable, Runnable cancelRunnable, long delay, long period);

	void repeatTask(String taskName, Runnable mainRunnable, Runnable cancelRunnable, long period);

	void repeatTask(String taskName, Runnable mainRunnable, long period);

	void delayedTask(String taskName, Runnable mainRunnable, Runnable cancelRunnable, long delay);

	void delayedTask(String taskName, Runnable mainRunnable, long delay);

	void cancelTask(String prefix);

}
