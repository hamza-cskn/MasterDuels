package mc.obliviate.masterduels.game;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.game.spectator.SpectatorStorage;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * this object uses locked field as lock
 * when locked field is not null, match team manager is locked.
 **/
public class MatchTeamManager {

    private final MatchDataStorage matchDataStorage;
    private boolean locked = false;
    // when locked, team builders is dysfunctional
    private final List<Team.Builder> teamBuilders = new ArrayList<>();
    // when unlocked, team is dysfunctional
    private final List<Team> teams = new ArrayList<>();
    private int teamAmount = 2;
    private int teamSize = 1;

    public MatchTeamManager(MatchDataStorage matchDataStorage) {
        this.matchDataStorage = matchDataStorage;
        createAllTeams();
    }

    public boolean areAllTeamBuildersFull() {
        for (Team.Builder team : teamBuilders) {
            if (team.getMemberBuilders().size() != teamSize) {
                return false;
            }
        }
        return true;
    }

    public List<Member> getAllMembers() {
        final List<Member> members = new ArrayList<>();
        for (final Team team : teams) {
            members.addAll(team.getMembers());
        }
        return members;
    }

    public List<Member.Builder> getAllMemberBuilders() {
        final List<Member.Builder> members = new ArrayList<>();
        for (Team.Builder team : teamBuilders) {
            members.addAll(team.getMemberBuilders());
        }
        return members;
    }

    public Member.Builder getMemberBuilder(UUID playerUniqueId) {
        for (Team.Builder team : teamBuilders) {
            for (Member.Builder member : team.getMemberBuilders()) {
                if (member.getPlayer().getUniqueId().equals(playerUniqueId)) {
                    return member;
                }
            }
        }
        return null;
    }

    public Member getMember(UUID playerUniqueId) {
        for (Team team : teams) {
            for (Member member : team.getMembers()) {
                if (member.getPlayer().getUniqueId().equals(playerUniqueId)) {
                    return member;
                }
            }
        }
        return null;
    }

    public boolean isMember(UUID playerUniqueId) {
        return getMember(playerUniqueId) != null;
    }

    public void unregisterMember(Member member) {
        member.getTeam().unregisterMember(member);
    }

    public void unregisterPlayer(Player player) {
        unregisterPlayer(UserHandler.getUser(player.getUniqueId()));
    }

    public void unregisterPlayer(IUser user) {
        for (Team.Builder builder : teamBuilders) {
            for (Member.Builder memberBuilder : builder.getMemberBuilders()) {
                if (user.equals(memberBuilder.getUser())) {
                    builder.unregisterPlayer(memberBuilder);
                    return;
                }
            }
        }
    }

    public void registerPlayer(Player player, Kit kit, int teamNo) {
        final Team.Builder teamBuilder = teamBuilders.get(teamNo - 1);
        if (teamBuilder == null)
            throw new IllegalStateException("member could not registered because team builder " + teamNo + " is not found");

        teamBuilder.registerPlayer(player, kit);
    }

    public void unregisterAllTeams() {
        Preconditions.checkState(!isLocked(), "this object is locked");
        teamBuilders.clear();
    }

    public void createAllTeams() {
        final List<Member.Builder> membersCopy = new ArrayList<>(getAllMemberBuilders());

        unregisterAllTeams();
        int safe = 0, memberIndex = 0;

        while (this.teamBuilders.size() < this.teamAmount) {
            if (++safe > 50) {
                throw new IllegalStateException(safe + " teams created. probably team create task repeated infinitely");
            }

            //create team
            Team.Builder builder = new Team.Builder(teamBuilders.size() + 1, teamSize);
            teamBuilders.add(builder);

            if (membersCopy.size() <= 0) continue;
            for (int index = teamSize; index > 0; index--) {
                if (memberIndex < membersCopy.size()) {
                    Member.Builder memBuilder = membersCopy.get(memberIndex);
                    builder.registerPlayer(memBuilder.getPlayer(), memBuilder.getKit(this.matchDataStorage.getKitManager().getKitMode()));
                    memberIndex++;
                }
            }
        }
        Bukkit.broadcastMessage("teams are ready: " + this);
    }

    public Team getTeam(Player player) {
        for (final Team team : teams) {
            for (Member member : team.getMembers()) {
                if (member.getPlayer().equals(player)) return team;
            }
        }
        return null;
    }

    public Team.Builder getTeamBuilder(Player player) {
        for (final Team.Builder teamBuilder : teamBuilders) {
            for (Member.Builder builder : teamBuilder.getMemberBuilders()) {
                if (builder.getPlayer().equals(player)) return teamBuilder;
            }
        }
        return null;
    }

    /**
     * @return null, if 2 teams survived still.
     */
    public Team getLastSurvivedTeam() {
        Team survivedTeam = null;
        for (Team team : teams) {
            if (!checkTeamEliminated(team)) {
                if (survivedTeam != null) return null;
                survivedTeam = team;
            }
        }
        return survivedTeam;
    }

    public boolean checkTeamEliminated(final Team team) {
        final SpectatorStorage omniSpectatorStorage = team.getMatch().getGameSpectatorManager().getSemiSpectatorStorage();
        LOOP1:
        for (Member member : team.getMembers()) {
            for (Spectator spectator : omniSpectatorStorage.getSpectatorList()) {
                if (member.getPlayer().equals(spectator.getPlayer())) {
                    continue LOOP1;
                }
            }
            return false;
        }
        return true;
    }

    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public List<Team.Builder> getTeamBuilders() {
        return Collections.unmodifiableList(teamBuilders);
    }

    public int getTeamAmount() {
        return teamAmount;
    }

    public void setTeamsAttributes(int size, int amount) {
        Preconditions.checkState(!isLocked(), "this object is locked");
        this.teamSize = size;
        this.teamAmount = amount;
        createAllTeams();
    }


    public int getTeamSize() {
        return teamSize;
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock(Match match) {
        teamBuilders.forEach(builder -> teams.add(builder.build(match)));
        teamBuilders.clear();
        this.locked = true;
    }
}
