package mc.obliviate.masterduels.arenaclear;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.IGame;
import mc.obliviate.masterduels.game.Game;

public interface IArenaClearHandler {

	void init();

	void add(IGame game, MasterDuels plugin);

	IArenaClear getArenaClear(String arenaName);
}
