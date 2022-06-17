package mc.obliviate.masterduels.bossbar;

import mc.obliviate.masterduels.api.user.IMember;

public interface IBossBarManager {

	void show(IMember member);

	void init();

	void finish();

	void hide(IMember member);
}
