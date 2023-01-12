package mc.obliviate.masterduels.user;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.Team;
import mc.obliviate.masterduels.game.creator.KitManager;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.playerdata.statistics.DuelStatistic;
import org.bukkit.entity.Player;

public class Member extends User implements IUser {

    private final Team team;
    private final Kit kit;

    Member(final Player player, final Team team, Kit kit, boolean inviteReceiving, boolean showScoreboard, boolean showBossBar, DuelStatistic statistic) {
        super(player, inviteReceiving, showScoreboard, showBossBar, statistic);
        this.team = team;
        this.kit = kit;
    }

    public Team getTeam() {
        return team;
    }

    public Match getMatch() {
        return team.getMatch();
    }

    public Kit getKit() {
        return kit;
    }

    @Override
    public void exitMatchBuilder() {
        super.exitMatchBuilder();
        UserHandler.switchUser(this);
    }

    public static class Builder {

        private final User user;
        private Kit kit;
        private Kit defaultKit; //it's ugly but I'll do it.

        public Builder(User user, Kit kit) {
            this.user = user;
            this.kit = kit;
        }

        public Member buildAndSwitch(Team team) {
            return UserHandler.switchMember(user, team, kit);
        }

        public Player getPlayer() {
            return user.getPlayer();
        }

        public User getUser() {
            return user;
        }

        public void setDefaultKit(Kit kit) {
            this.defaultKit = kit;
        }

        public void setKit(Kit kit) {
            this.kit = kit;
        }

        public Kit getKit() {
            return getKit(KitManager.KitMode.MUTUAL);
        }

        public Kit getKit(KitManager.KitMode mode) {
            if (mode == KitManager.KitMode.VARIOUS) {
                if (kit == null) return defaultKit;
                return kit;
            } else {
                return defaultKit;
            }
        }
    }
}
