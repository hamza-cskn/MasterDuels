package mc.obliviate.masterduels.listeners;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.game.creator.MatchCreator;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

	private final MasterDuels plugin;

	public PlayerConnectionListener(MasterDuels plugin) {
		this.plugin = plugin;
	}

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        final IUser user = UserHandler.getUser(event.getPlayer().getUniqueId());
        if (user == null) return;

        if (!MasterDuels.isInShutdownMode()) {
            plugin.getSqlManager().saveUser(user);
        }

        if (user instanceof Member) {
            ((Member) user).getTeam().getMatch().getMatchState().leave(((Member) user));
        } else if (user.getMatchBuilder() != null) {
            MatchCreator creator = MatchCreator.getCreator(event.getPlayer().getUniqueId());
            if (creator == null || creator.getOwnerPlayer() != event.getPlayer().getUniqueId() || !MatchCreator.cleanKillCreator(event.getPlayer().getUniqueId())) {
                user.getMatchBuilder().removePlayer(user);
            }
        }
    }

	@EventHandler
	public void onConnect(PlayerJoinEvent event) {
		UserHandler.loadDuelUser(plugin.getSqlManager(), event.getPlayer());
	}

}
