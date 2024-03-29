package mc.obliviate.masterduels.game.state;

import mc.obliviate.masterduels.api.DuelMatchMemberDeathEvent;
import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchStateType;
import mc.obliviate.masterduels.game.Team;
import mc.obliviate.masterduels.kit.InventoryStorer;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageEvent;

import static mc.obliviate.masterduels.kit.Kit.USE_PLAYER_INVENTORIES;

public class PlayingState implements MatchState {

    private final Match match;

    public PlayingState(Match game) {
        this.match = game;
    }

    @Override
    public void next() {
        if (!match.getMatchState().equals(this)) return;
        if (match.getGameDataStorage().getRoundData().nextRound()) {
            match.setGameState(new RoundEndingState(match));
        } else {
            match.setGameState(new MatchEndingState(match));
        }
    }

    @Override
    public void onDamage(EntityDamageEvent event, Member victim, Member attacker) {
        if (event.getFinalDamage() >= victim.getPlayer().getHealth()) {
            event.setCancelled(true);
            onDeath(event, victim, attacker);
        }
    }

    private void onDeath(EntityDamageEvent event, Member victim, Member attacker) {
        Bukkit.getPluginManager().callEvent(new DuelMatchMemberDeathEvent(event, victim, attacker));

        match.getGameSpectatorManager().spectate(victim);

        if (attacker == null) {
            match.broadcastInGame("player-dead.without-attacker", new PlaceholderUtil().
                    add("{victim}", Utils.getDisplayName(victim.getPlayer())));
        } else {
            match.broadcastInGame("player-dead.by-attacker", new PlaceholderUtil()
                    .add("{attacker-health}", attacker.getPlayer().getHealthScale() + "")
                    .add("{attacker}", Utils.getDisplayName(attacker.getPlayer()))
                    .add("{victim}", Utils.getDisplayName(victim.getPlayer())));
        }

        if (match.getGameDataStorage().getGameTeamManager().checkTeamEliminated(victim.getTeam())) {
            if (match.getGameDataStorage().getGameTeamManager().getTeamSize() != 1) {
                match.broadcastInGame("duel-team-eliminated", new PlaceholderUtil().add("{victim}", Utils.getDisplayName(victim.getPlayer())));
            }
        }
        eliminateCheckup();
    }

    private void eliminateCheckup() {
        final Team lastSurvivedTeam = match.getGameDataStorage().getGameTeamManager().getLastSurvivedTeam();
        if (lastSurvivedTeam != null) {
            match.getGameDataStorage().getGameRoundData().addWin(lastSurvivedTeam);
            match.getGameDataStorage().getGameRoundData().setWinnerTeam(lastSurvivedTeam);
            next();
        }
    }

    @Override
    public void leave(Member member) {
        if (!member.getTeam().getMembers().contains(member)) return;

        Bukkit.getPluginManager().callEvent(new DuelMatchMemberLeaveEvent(member));
        InventoryStorer.restore(member.getPlayer());
        member.getMatch().removeMember(member);

        if (!USE_PLAYER_INVENTORIES) InventoryStorer.restore(member.getPlayer());

        if (member.getTeam().getMembers().size() == 0) {
            if (match.getAllMembers().size() > 0) {
                match.finish();
            } else {
                match.uninstall();
            }
        }
        eliminateCheckup();

        if (member.getPlayer().isOnline()) {
            match.showAll(member.getPlayer());
            Match.RESET_WHEN_PLAYER_LEFT.reset(member.getPlayer());
            Utils.teleportToLobby(member.getPlayer());
            MessageUtils.sendMessage(member.getPlayer(), "you-left-from-duel");
        }
    }

    @Override
    public Match getMatch() {
        return match;
    }

    @Override
    public MatchStateType getMatchStateType() {
        return MatchStateType.PLAYING;
    }

}
