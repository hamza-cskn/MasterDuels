package mc.obliviate.masterduels.utils.scoreboard;

import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.IMatch;
import mc.obliviate.masterduels.api.arena.MatchStateType;
import mc.obliviate.masterduels.api.user.IMember;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {

	private static final Map<MatchStateType, ScoreboardFormatConfig> scoreboardLines = new HashMap<>();
	private static TitleManagerAPI api;
	private final MasterDuels plugin;

	public static Map<MatchStateType, ScoreboardFormatConfig> getScoreboardLines() {
		return scoreboardLines;
	}

	public ScoreboardManager(final MasterDuels plugin) {
		this.plugin = plugin;
		TitleManagerAPI api = null;
		//plugin.getDatabaseHandler().getConfig().getBoolean("scoreboard.is-enabled")
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("TitleManager")) {
			api = (TitleManagerAPI) Bukkit.getServer().getPluginManager().getPlugin("TitleManager");
			plugin.getLogger().info("TitleManager plugin found. The API successfully hooked.");
		}

		ScoreboardManager.api = api;

	}

	private static boolean isAPIDisabled() {
		return api == null;
	}

	public static void update(final Player player, boolean reinstall) {
		if (isAPIDisabled() || player == null) return;

		final IMember member = DataHandler.getMember(player.getUniqueId());
		if (member == null) {
			defaultScoreboard(player);
		} else {
			update(member, reinstall);
		}
	}

	public static void uninstall(final Player player) {
		api.removeScoreboard(player);
	}

	public static void update(final IMember member, boolean reinstall) {
		if (isAPIDisabled() || member == null) return;
		final Player player = member.getPlayer();
		final IMatch game = member.getTeam().getMatch();

		if (reinstall) {
			uninstall(player);
		}
		api.giveScoreboard(player);

		int index = 0;
		final ScoreboardFormatConfig scoreboardFormatConfig = scoreboardLines.get(game.getMatchState());

		api.setScoreboardTitle(player, scoreboardFormatConfig.getTitle());

		for (final String line : scoreboardFormatConfig.getLines()) {
			if (line.equalsIgnoreCase("{+opponents}")) {
				for (final IMember m : game.getAllMembers()) {
					if (!m.getTeam().equals(m.getTeam())) {
						if (m.getPlayer().isOnline() || game.getGameSpectatorManager().isSpectator(m.getPlayer())) {
							api.setProcessedScoreboardValue(player, ++index, MessageUtils.parseColor(scoreboardFormatConfig.getDeadOpponentFormat().replace("{health}", "0").replace("{name}", m.getPlayer().getName() + "")));
						} else {
							api.setProcessedScoreboardValue(player, ++index, MessageUtils.parseColor(scoreboardFormatConfig.getLiveOpponentFormat().replace("{health}", "" + m.getPlayer().getHealthScale()).replace("{name}", m.getPlayer().getName() + "")));
						}
					}
				}
			} else {
				api.setProcessedScoreboardValue(player, ++index, MessageUtils.parseColor(line
						.replace("{round}", "" + game.getGameDataStorage().getGameRoundData().getCurrentRound())
						.replace("{map}", game.getArena().getMapName() + "")
						.replace("{timer}", TimerUtils.formatTimeUntilThenAsTimer(game.getGameDataStorage().getFinishTime()))
						.replace("{team-size}", member.getTeam().getMembers().size() + "")));
			}
		}
	}

	public static void defaultScoreboard(final Player player) {
		if (isAPIDisabled() || player == null || !player.isOnline()) return;
		api.removeScoreboard(player);
		api.giveDefaultScoreboard(player);
	}


}
