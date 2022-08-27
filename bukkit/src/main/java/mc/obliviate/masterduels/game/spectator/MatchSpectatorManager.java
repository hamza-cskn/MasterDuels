package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class MatchSpectatorManager {

    private final SemiSpectatorStorage semiSpectatorStorage;
    private final PureSpectatorStorage pureSpectatorStorage;
    private final Match match;

    public MatchSpectatorManager(Match match) {
        this.match = match;
        this.semiSpectatorStorage = new SemiSpectatorStorage(this);
        this.pureSpectatorStorage = new PureSpectatorStorage(this);
    }

    public SemiSpectatorStorage getSemiSpectatorStorage() {
        return semiSpectatorStorage;
    }

    public PureSpectatorStorage getPureSpectatorStorage() {
        return pureSpectatorStorage;
    }

    public void spectate(Member member) {
        semiSpectatorStorage.spectate(member.getPlayer());
    }

    public void spectate(Player player) {
        if (match.getGameDataStorage().getGameTeamManager().getMember(player.getUniqueId()) != null) {
            semiSpectatorStorage.spectate(player);
            return;
        }
        pureSpectatorStorage.spectate(player);
    }

    public void unspectate(Spectator spectator) {
        unspectate(spectator, true);
    }

    public boolean isPure(Spectator spectator) {
        return !match.getPlayers().contains(spectator.getPlayer());
    }

    public void unspectate(Spectator spectator, boolean toMember) {
        if (isPure(spectator)) {
            pureSpectatorStorage.unspectate(spectator);
        } else {
            semiSpectatorStorage.unspectate(spectator, toMember);
        }
    }

    public List<Spectator> getAllSpectators() {
        final List<Spectator> spectators = new ArrayList<>(semiSpectatorStorage.getSpectatorList());
        spectators.addAll(pureSpectatorStorage.getSpectatorList());
        return spectators;
    }

    protected Match getMatch() {
        return match;
    }
}
