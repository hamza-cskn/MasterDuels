package mc.obliviate.masterduels.game.creator;

import mc.obliviate.masterduels.game.MatchTeamManager;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.Member;

public class KitManager {

	private final MatchTeamManager teamManager;
	private Kit defaultKit = null;
	private KitMode kitMode = KitMode.MUTUAL;

	public KitManager(MatchTeamManager teamManager) {
		this.teamManager = teamManager;
	}

	public void setDefaultKit(Kit defaultKit) {
		this.defaultKit = defaultKit;

		for (final Member.Builder builder : this.teamManager.getAllMemberBuilders()) {
			builder.setDefaultKit(defaultKit);
		}
	}

	public Kit getDefaultKit() {
		return defaultKit;
	}

	public void setKitMode(KitMode kitMode) {
		this.kitMode = kitMode;
	}

	public KitMode getKitMode() {
		return kitMode;
	}

	public enum KitMode {
		MUTUAL,
		VARIOUS
	}
}
