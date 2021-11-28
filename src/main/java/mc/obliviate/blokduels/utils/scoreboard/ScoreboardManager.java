package mc.obliviate.blokduels.utils.scoreboard;

import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameState;
import mc.obliviate.blokduels.team.Member;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {

	private static final Map<GameState, ScoreboardFormatConfig> scoreboardLines = new HashMap<>();
	private static TitleManagerAPI api;
	private final BlokDuels plugin;

	public static Map<GameState, ScoreboardFormatConfig> getScoreboardLines() {
		return scoreboardLines;
	}

	public ScoreboardManager(final BlokDuels plugin) {
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

		final Member member = DataHandler.getMember(player.getUniqueId());
		if (member == null) {
			defaultScoreboard(player);
		} else {
			update(member, reinstall);
		}
	}

	public static void uninstall(final Player player) {
		api.removeScoreboard(player);
	}

	public static void update(final Member member, boolean reinstall) {
		if (isAPIDisabled() || member == null) return;
		final Player player = member.getPlayer();
		final Game game = member.getTeam().getGame();

		if (reinstall) {
			uninstall(player);
		}
		api.giveScoreboard(player);

		int index = 0;
		final ScoreboardFormatConfig scoreboardFormatConfig = scoreboardLines.get(game.getGameState());

		api.setScoreboardTitle(player, scoreboardFormatConfig.getTitle());

		for (final String line : scoreboardFormatConfig.getLines()) {
			if (line.equalsIgnoreCase("{+opponents}")) {
				for (final Member m : game.getAllMembers()) {
					if (!member.getTeam().equals(m.getTeam())) {
						if (game.getSpectatorData().isSpectator(m.getPlayer())) {
							api.setProcessedScoreboardValue(player, ++index, MessageUtils.parseColor(scoreboardFormatConfig.getDeadOpponentFormat().replace("{health}", "0").replace("{name}", m.getPlayer().getName() + "")));
						} else {
							api.setProcessedScoreboardValue(player, ++index, MessageUtils.parseColor(scoreboardFormatConfig.getLiveOpponentFormat().replace("{health}", "" + m.getPlayer().getHealthScale()).replace("{name}", m.getPlayer().getName() + "")));
						}
					}
				}

			} else {
				api.setProcessedScoreboardValue(player, ++index, MessageUtils.parseColor(line
						.replace("{round}", "" + game.getRoundData().getCurrentRound())
						.replace("{map}", game.getArena().getMapName() + "")
						.replace("{timer}", TimerUtils.convertTimer(game.getTimer()))
						.replace("{placed-blocks}", "" + game.getPlacedBlocks().size())
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
