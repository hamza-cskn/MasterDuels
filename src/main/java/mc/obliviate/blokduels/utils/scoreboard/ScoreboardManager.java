package mc.obliviate.blokduels.utils.scoreboard;

import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameState;
import mc.obliviate.blokduels.team.Member;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.timer.TimerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ScoreboardManager {

	private static TitleManagerAPI api;
	private final BlokDuels plugin;

	public ScoreboardManager(final BlokDuels plugin) {
		this.plugin = plugin;
		TitleManagerAPI api = null;
		//plugin.getDatabaseHandler().getConfig().getBoolean("scoreboard.is-enabled")
		if (true && Bukkit.getServer().getPluginManager().isPluginEnabled("TitleManager")) {
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
		api.setScoreboardTitle(player, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Duel");

		final String opponentFormat = "&6{name} &c{health}❤";
		if (game.getGameState() == GameState.BATTLE) {
			for (final String line : Arrays.asList("", "&aHarita: &f{map}", "&aRaund: &c#{round}", "&aTakım: &f{team-size} kişi", "", "{opponents}", "", "&ewww.blokdunyasi.net")) {
				if (line.equalsIgnoreCase("{opponents}")) {

					for (Member m : game.getAllMembers()) {
						if (!member.getTeam().equals(m.getTeam())) {
							api.setProcessedScoreboardValue(player, ++index,
									MessageUtils.parseColor(
											opponentFormat.replace("{health}", "" + m.getPlayer().getHealth())
													.replace("{name}", m.getPlayer().getName() + "")
									));
						}
					}

				} else {

					api.setProcessedScoreboardValue(player, ++index,
							MessageUtils.parseColor(

									line.replace("{round}", "" + game.getRoundData().getCurrentRound())
											.replace("{map}", game.getArena().getMapName() + "")
											.replace("{team-size}", member.getTeam().getMembers().size() + ""
											)));
				}


			}
		} else if (game.getGameState() == GameState.ROUND_STARTING) {
			for (final String line : Arrays.asList("", "&aHarita: &f{map}", "&aRaund: &c#{round}", "&aTakım: &f{team-size} kişi", "", "&aBaşlıyor: &f{timer}", "", "&ewww.blokdunyasi.net")) {
				api.setProcessedScoreboardValue(player, ++index,
						MessageUtils.parseColor(
								line.replace("{round}", "" + game.getRoundData().getCurrentRound())
										.replace("{map}", game.getArena().getMapName() + "")
										.replace("{timer}", TimerUtils.convertTimer(game.getTimer()))
										.replace("{team-size}", member.getTeam().getMembers().size() + ""
										)));

			}
		}


	}

	public static void defaultScoreboard(final Player player) {
		if (isAPIDisabled() || player == null || !player.isOnline()) return;
		api.removeScoreboard(player);
		api.giveDefaultScoreboard(player);
	}


}
