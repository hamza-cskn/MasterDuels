package mc.obliviate.masterduels.game.spectator;

import mc.obliviate.masterduels.api.spectator.DuelMatchPreSpectatorJoinEvent;
import mc.obliviate.masterduels.api.spectator.DuelMatchSpectatorSwitchEvent;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.Team;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.playerreset.PlayerReset;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SemiSpectatorStorage implements SpectatorStorage {

    protected static final PlayerReset playerReset = new PlayerReset().excludeExp().excludeLevel().excludeInventory().excludeTitle();
    private final MatchSpectatorManager gsm;
    private final List<Spectator> spectators = new ArrayList<>();
    private final Match match;

    public SemiSpectatorStorage(MatchSpectatorManager gsm) {
        this.gsm = gsm;
        this.match = gsm.getMatch();
    }

    private Spectator findSpectator(Player player) {
        for (final Spectator spectator : spectators) {
            if (spectator.getPlayer().equals(player)) {
                return spectator;
            }
        }
        return null;
    }

    @Override
    public void unspectate(Spectator spectator) {
        unspectate(spectator, true);
    }

    @Override
    public void unspectate(Player player) {
        final Spectator spectator = findSpectator(player);
        if (spectator == null) return;
        unspectate(spectator);
    }

    public void unspectate(Spectator spectator, boolean toMember) {
        if (!spectators.remove(spectator)) return;

        if (toMember) {
            Team team = match.getGameDataStorage().getGameTeamManager().getTeam(spectator.getPlayer());
            UserHandler.switchMember(spectator, team, null);
        } else {
            UserHandler.switchUser(spectator);
            match.removeMember(match.getMember(spectator.getPlayer().getUniqueId()));
        }
        playerReset.reset(spectator.getPlayer());
        MessageUtils.sendMessage(spectator.getPlayer(), "you-left-from-duel");
        Utils.teleportToLobby(spectator.getPlayer());

        for (final Member member : match.getAllMembers()) {
            member.getPlayer().showPlayer(spectator.getPlayer());
        }
    }

    @Override
    public void spectate(Player player) {
        Spectator spectator = findSpectator(player);
        if (spectator != null) return;

        final DuelMatchPreSpectatorJoinEvent duelGamePreSpectatorJoinEvent = new DuelMatchPreSpectatorJoinEvent(player, match);
        Bukkit.getPluginManager().callEvent(duelGamePreSpectatorJoinEvent);
        if (duelGamePreSpectatorJoinEvent.isCancelled()) return;

        spectator = UserHandler.switchSpectator(UserHandler.getUser(player.getUniqueId()), match);
        spectators.add(spectator);

        final DuelMatchSpectatorSwitchEvent duelMatchSpectatorSwitchEvent = new DuelMatchSpectatorSwitchEvent(spectator);
        Bukkit.getPluginManager().callEvent(duelMatchSpectatorSwitchEvent);

        //SpectatorInventoryHandler.giveSpectatorItems(player);
		new PlayerReset().excludeGamemode().excludeInventory().excludeLevel().excludeExp().reset(player);
		player.setGameMode(GameMode.ADVENTURE);

        for (final Spectator spec : gsm.getAllSpectators()) {
            spec.getPlayer().showPlayer(player);
            player.showPlayer(spec.getPlayer());
        }

        for (final Member member : match.getAllMembers()) {
            member.getPlayer().hidePlayer(player);
        }

        player.setAllowFlight(true);
        player.setFlying(true);

        //won't teleport
        MessageUtils.sendMessage(player, "you-are-a-spectator");
        //MessageAPI.getInstance(game.getPlugin()).sendTitle(player, TitleHandler.getTitle(TitleHandler.TitleType.SPECTATOR_JOIN));
    }

    @Override
    public boolean isSpectator(Player player) {
        for (Spectator spectator : spectators) {
            if (spectator.getPlayer().equals(player)) return true;
        }
        return false;
    }

    @Override
    public List<Spectator> getSpectatorList() {
        return spectators;
    }
}
