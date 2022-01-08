package mc.obliviate.masterduels.game.types;

public enum GameMode {

	SOLO(1),
	DOUBLES(2),
	TRIPLES(3),
	QUADRUPLE(4);


	private final int teamSize;

	GameMode(final int teamSize) {
		this.teamSize = teamSize;
	}
}
