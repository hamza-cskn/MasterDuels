package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.Team;
import mc.obliviate.masterduels.kit.Kit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserHandler {

	private static final Map<UUID, IUser> USER_MAP = new HashMap<>();

	public static void loadDuelUser(SQLManager sqlManager, Player player) {
		registerUser(player.getUniqueId(), User.loadDuelUser(sqlManager, player));
	}

	private static void registerUser(UUID uuid, IUser user) {
		USER_MAP.put(uuid, user);
	}


	public static Member getMember(UUID playerUniqueId) {
		final IUser user = getUser(playerUniqueId);
		if (user instanceof Member) {
			return (Member) user;
		}
		return null;
	}

	public static Spectator getSpectator(UUID playerUniqueId) {
		final IUser user = getUser(playerUniqueId);
		if (user instanceof Spectator) {
			return (Spectator) user;
		}
		return null;
	}

	public static IUser getUser(UUID playerUniqueId) {
		return USER_MAP.get(playerUniqueId);
	}

	public static Member switchMember(IUser user, Team team, Kit kit) {
		if (user instanceof Member) return (Member) user;
		final Member member = new Member(user.getPlayer(), team, kit, user.inviteReceiving(), user.getStatistic());
		registerUser(user.getPlayer().getUniqueId(), member);
		return member;
	}

	public static User switchUser(IUser user) {
		if (user instanceof User) return (User) user;
		final User rUser = new User(user.getPlayer(), user.inviteReceiving(), user.getStatistic());
		registerUser(user.getPlayer().getUniqueId(), rUser);
		return rUser;
	}

	public static Spectator switchSpectator(IUser user, Match match) {
		if (user instanceof Spectator) return (Spectator) user;
		final Spectator spectator = new Spectator(user.getPlayer(), match, user.inviteReceiving(), user.getStatistic());
		registerUser(user.getPlayer().getUniqueId(), spectator);
		return spectator;
	}

	public static boolean isMember(UUID playerUniqueId) {
		return getUser(playerUniqueId) instanceof Member;
	}

	public static boolean isSpectator(UUID playerUniqueId) {
		return getUser(playerUniqueId) instanceof Spectator;
	}

}
