package mc.obliviate.masterduels.api.arena;

import mc.obliviate.masterduels.api.user.ITeam;
import org.bukkit.entity.Player;

import java.util.List;

public interface ITeamBuilder {

	int getSize();

	List<Player> getMembers();

	int getTeamId();

	void add(Player p);

	void remove(Player p);

	ITeam build(IGame game);

}
