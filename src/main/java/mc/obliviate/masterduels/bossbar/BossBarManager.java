package mc.obliviate.masterduels.bossbar;

import mc.obliviate.masterduels.user.team.Member;

public interface BossBarManager {

	void show(Member member);
	void init();
	void finish();
	void hide(Member member);
}
