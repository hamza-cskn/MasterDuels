package mc.obliviate.masterduels.utils.tab;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.DuelMatchMemberLeaveEvent;
import mc.obliviate.masterduels.api.arena.DuelMatchStartEvent;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import me.neznamy.tab.api.TabAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class NameTagManager implements Listener {

	public NameTagManager(MasterDuels plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		register();
	}

	@EventHandler
	public void onMatchStart(DuelMatchStartEvent event) {
		for (Member member : event.getMatch().getAllMembers()) {
			activateAboveName(member.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onLeave(DuelMatchMemberLeaveEvent event) {
		deactivateAboveName(event.getMember().getPlayer().getUniqueId());
	}

	public static void activateAboveName(UUID uuid) {
		TabAPI.getInstance().getTeamManager().setPrefix(TabAPI.getInstance().getPlayer(uuid), "%rel_masterduels_prefix%");
	}

	public static void deactivateAboveName(UUID uuid) {
		TabAPI.getInstance().getTeamManager().resetPrefix(TabAPI.getInstance().getPlayer(uuid));
	}

	private static void register() {
		final String friendPrefix = ConfigurationHandler.getConfig().getString("tab-nametags.friend-prefix");
		final String opponentPrefix = ConfigurationHandler.getConfig().getString("tab-nametags.opponent-prefix");
		final String thirdPersonPrefix = ConfigurationHandler.getConfig().getString("tab-nametags.third-person-prefix");
		TabAPI.getInstance().getPlaceholderManager().registerRelationalPlaceholder("%rel_masterduels_prefix%", 2000, (viewer, target) -> {
			Member targetMember = UserHandler.getMember(target.getUniqueId());
			if (targetMember == null) return "";
			Member viewerMember = UserHandler.getMember(viewer.getUniqueId());
			if (viewerMember == null) return thirdPersonPrefix;

			if (viewerMember.getTeam().equals(targetMember.getTeam())) return friendPrefix;
			return opponentPrefix;

		});

	}

}
