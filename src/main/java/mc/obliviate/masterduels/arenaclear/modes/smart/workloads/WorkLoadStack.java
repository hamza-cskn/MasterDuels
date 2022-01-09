package mc.obliviate.masterduels.arenaclear.modes.smart.workloads;

import java.util.List;
import java.util.UUID;

public class WorkLoadStack {

	private final List<IWorkLoad> workLoads;

	public WorkLoadStack(List<IWorkLoad> workLoads) {
		this.workLoads = workLoads;
	}

	public List<IWorkLoad> getWorkLoads() {
		return workLoads;
	}

	public boolean contains(int x, int y, int z, UUID worldUID) {
		for (final IWorkLoad workLoad : workLoads) {
			if (workLoad.equals(x, y, z, worldUID)) {
				return true;
			}
		}
		return false;
	}

}
