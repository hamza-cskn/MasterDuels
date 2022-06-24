package mc.obliviate.masterduels.game.task;

import mc.obliviate.masterduels.MasterDuels;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class GameTask implements BukkitTask {

	/**
	 * task names defines purpose of task
	 * and task manager can cancel tasks using
	 * their names.
	 * <p>
	 * task manager cancels tasks with checking their
	 * first part of names. for example,
	 * <p>
	 * TASK 1: round.end-timer-task<br>
	 * TASK 2: round.ending-task<br>
	 * TASK 3: round.scoreboard-task
	 * <p>
	 * when you call cancelTasks("round")
	 * task manager checks first part of
	 * the tasks and it will cancel all of them.
	 * <p>
	 * when you call cancelTasks("round.e")
	 * task manager will cancel all tasks
	 * except "round.scoreboard-task"
	 */
	private final String taskName;

	private final BukkitTask task;
	private final Runnable cancelRunnable;

	/**
	 * repeat task constructor
	 *
	 * @param delay  how many ticks after task will run
	 * @param period repeat period as ticks
	 */
	protected GameTask(String taskName, Runnable mainRunnable, Runnable cancelRunnable, long delay, long period) {
		this.taskName = taskName;
		this.task = Bukkit.getScheduler().runTaskTimer(JavaPlugin.getProvidingPlugin(MasterDuels.class), mainRunnable, delay, period);
		this.cancelRunnable = cancelRunnable;
	}

	/**
	 * delayed task constructor
	 *
	 * @param delay how many ticks after task will run
	 */
	protected GameTask(String taskName, Runnable mainRunnable, Runnable cancelRunnable, long delay) {
		this.taskName = taskName;
		this.task = Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(MasterDuels.class), mainRunnable, delay);
		this.cancelRunnable = cancelRunnable;
	}

	public String getTaskName() {
		return taskName;
	}

	@Override
	public int getTaskId() {
		return task.getTaskId();
	}

	@Override
	public Plugin getOwner() {
		return task.getOwner();
	}

	@Override
	public boolean isSync() {
		return task.isSync();
	}

	@Override
	public void cancel() {
		task.cancel();
		if (cancelRunnable != null)
			cancelRunnable.run();
	}
}
