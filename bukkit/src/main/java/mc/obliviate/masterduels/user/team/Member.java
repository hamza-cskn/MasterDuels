package mc.obliviate.masterduels.user.team;

import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.kit.IKit;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.api.user.ITeam;
import mc.obliviate.masterduels.data.DataHandler;
import org.bukkit.entity.Player;

public class Member implements IMember {

	private final Player player;
	private final ITeam team;
	private final IKit kit;

	public Member(final Player player, final ITeam team, IKit kit) {
		this.team = team;
		this.player = player;
		this.kit = kit;
		DataHandler.getUsers().put(player.getUniqueId(), this);
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public ITeam getTeam() {
		return team;
	}

	@Override
	public IMatch getGame() {
		return team.getMatch();
	}

	@Override
	public IKit getKit() {
		return kit;
	}

}
