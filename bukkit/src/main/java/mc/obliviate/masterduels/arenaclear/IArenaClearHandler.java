package mc.obliviate.masterduels.arenaclear;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.IMatch;

public interface IArenaClearHandler {

	void init();

	void add(IMatch match, MasterDuels plugin);

	IArenaClear getArenaClear(String arenaName);
}
