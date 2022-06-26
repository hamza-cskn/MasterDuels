package mc.obliviate.masterduels.user;

public interface DuelSpace {

	default void join(DuelUser duelUser) {
		if (duelUser.isInDuelSpace())
			throw new IllegalStateException("a player can join only one duel space at a moment.");
		duelUser.setDuelSpace(this);
	}

	default void leave(DuelUser duelUser) {
		duelUser.exitDuelSpace();
	}

}
