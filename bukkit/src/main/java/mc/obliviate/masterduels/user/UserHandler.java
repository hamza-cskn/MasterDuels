package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.data.SQLManager;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.Team;
import mc.obliviate.masterduels.game.creator.MatchCreator;
import mc.obliviate.masterduels.kit.Kit;
import org.bukkit.entity.Player;

import java.util.Collections;
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

    public static boolean isAvailableForJoinToBuilder(Player player) {
        final IUser user = UserHandler.getUser(player.getUniqueId());
        if (user == null) return false;
        if (user instanceof Member) return false;

        if (user.isInMatchBuilder()) {
            MatchCreator creator = MatchCreator.getCreator(player.getUniqueId());
            if (creator != null && creator.getOwnerPlayer().equals(player.getUniqueId())) {
                MatchCreator.cleanKillCreator(player.getUniqueId());
                return true;
            }
            return false;
        }
        return true;
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
        final Member member = new Member(user.getPlayer(), team, kit, user.inviteReceiving(), user.showScoreboard(), user.showBossBar(), user.getStatistic());
        registerUser(user.getPlayer().getUniqueId(), member);
        return member;
    }

    public static User switchUser(IUser user) {
        final User rUser = new User(user.getPlayer(), user.inviteReceiving(), user.showScoreboard(), user.showBossBar(), user.getStatistic());
        registerUser(user.getPlayer().getUniqueId(), rUser);
        return rUser;
    }

    public static Spectator switchSpectator(IUser user, Match match) {
        final Spectator spectator = new Spectator(user.getPlayer(), match, user.inviteReceiving(), user.showScoreboard(), user.showBossBar(), user.getStatistic());
        registerUser(user.getPlayer().getUniqueId(), spectator);
        return spectator;
    }

    public static boolean isMember(UUID playerUniqueId) {
        return getUser(playerUniqueId) instanceof Member;
    }

    public static boolean isSpectator(UUID playerUniqueId) {
        return getUser(playerUniqueId) instanceof Spectator;
    }

    public static Map<UUID, IUser> getUserMap() {
        return Collections.unmodifiableMap(USER_MAP);
    }
}
