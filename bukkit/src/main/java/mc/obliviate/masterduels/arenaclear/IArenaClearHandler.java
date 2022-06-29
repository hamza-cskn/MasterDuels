package mc.obliviate.masterduels.arenaclear;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.game.Match;

public interface IArenaClearHandler {

	void init();

	void add(Match match, MasterDuels plugin);

	IArenaClear getArenaClear(String arenaName);
}
