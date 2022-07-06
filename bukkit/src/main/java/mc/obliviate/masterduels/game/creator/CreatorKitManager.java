package mc.obliviate.masterduels.game.creator;

import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.Member;

public class CreatorKitManager {

	private final MatchCreator creator;
	private Kit defaultKit = null;
	private KitMode kitMode = KitMode.MUTUAL;

	public CreatorKitManager(MatchCreator creator) {
		this.creator = creator;
	}

	public void setDefaultKit(Kit defaultKit) {
		this.defaultKit = defaultKit;

		if (kitMode.equals(KitMode.MUTUAL)) {
			for (final Member.Builder builder : creator.getBuilder().getData().getGameTeamManager().getAllMemberBuilders()) {
				builder.setKit(defaultKit);
			}
		} else {
			for (final Member.Builder builder : creator.getBuilder().getData().getGameTeamManager().getAllMemberBuilders()) {
				if (builder.getKit() == null) {
					builder.setDefaultKit(defaultKit);
				}
			}
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
