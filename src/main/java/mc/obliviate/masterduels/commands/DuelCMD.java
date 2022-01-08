package mc.obliviate.masterduels.commands;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.Game;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.gui.DuelArenaListGUI;
import mc.obliviate.masterduels.gui.room.DuelGameCreatorGUI;
import mc.obliviate.masterduels.gui.DuelHistoryLogGUI;
import mc.obliviate.masterduels.gui.kit.KitSelectionGUI;
import mc.obliviate.masterduels.invite.Invite;
import mc.obliviate.masterduels.invite.InviteResult;
import mc.obliviate.masterduels.invite.Invites;
import mc.obliviate.masterduels.statistics.DuelStatistic;
import mc.obliviate.masterduels.user.User;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelCMD implements CommandExecutor {

	private final MasterDuels plugin;

	public DuelCMD(MasterDuels plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if (!(sender instanceof Player)) return false;

		final Player player = ((Player) sender).getPlayer();

		final User user = DataHandler.getUser(player.getUniqueId());

		if (args.length == 0) {
			MessageUtils.sendMessage(player, "duel-command.usage");
			return false;
		}

		if (args[0].equalsIgnoreCase("history")) {
			new DuelHistoryLogGUI(player).open();
			return true;
		}

		if (args[0].equalsIgnoreCase("leave")) {
			if (user == null) {
				MessageUtils.sendMessage(player, "you-are-not-in-duel");
				return false;
			}
			user.getGame().leave(user);
			return true;
		}

		//THESE COMMANDS BLOCKED FOR PLAYERS WHO IN DUEL

		if (user instanceof Member) {
			MessageUtils.sendMessage(player, "you-are-in-duel");
			return false;
		}

		if (args[0].equalsIgnoreCase("toggle")) {
			final boolean state = plugin.getSqlManager().toggleReceivesInvites(player.getUniqueId());
			if (state) {
				MessageUtils.sendMessage(player, "invite.toggle.turned-on");
			} else {
				MessageUtils.sendMessage(player, "invite.toggle.turned-off");
			}
			return true;
		}

		if (args[0].equalsIgnoreCase("stats")) {
			stats(player, args);
			return true;

		} else if (args[0].equalsIgnoreCase("arenas")) {
			new DuelArenaListGUI(player).open();
			return true;
		} else if (args[0].equalsIgnoreCase("accept")) {
			answerInvite(player, true, args);
			return true;
		} else if (args[0].equalsIgnoreCase("decline")) {
			answerInvite(player, false, args);
			return true;
		} else if (args[0].equalsIgnoreCase("spectate")) {
			spectate(player, args);
		} else if (args[0].equalsIgnoreCase("creator")) {
			GameBuilder gameBuilder = GameBuilder.getGameBuilderMap().get(player.getUniqueId());
			if (gameBuilder == null) gameBuilder = new GameBuilder(plugin, player.getUniqueId());
			new DuelGameCreatorGUI(player, gameBuilder).open();
		} else if (args.length == 1 || args[0].equalsIgnoreCase("invite")) {
			invite(player, args);
		}


		return true;

	}

	private void stats(final Player player, final String[] args) {
		final UUID target = args.length == 1 ? player.getUniqueId() : Bukkit.getOfflinePlayer(args[1]).getUniqueId();
		final DuelStatistic statistic = plugin.getSqlManager().getStatistic(target);
		MessageUtils.sendMessage(player, "statistics",
				new PlaceholderUtil()
						.add("{wins}", statistic.getWins() + "").add("{loses}", statistic.getLoses() + ""));

	}

	private void answerInvite(final Player player, final boolean answer, final String[] args) {
		final Invites invites = Invite.findInvites(player);
		if (invites == null || invites.size() == 0) {
			MessageUtils.sendMessage(player, "invite.you-dont-have-invite");
			return;
		}
		if (invites.size() == 1) {
			invites.get(0).setResult(answer);
			return;
		}
		if (args.length == 1) {
			int index = 0;
			for (final Invite invite : invites.getInvites()) {
				player.sendMessage(MessageUtils.parseColor("&8- &f/duel " + args[0] + " &7" + ++index + " -> " + invite.getInviter().getName()) + " (" + invite.getFormattedExpireTimeLeft() + ")");
			}
		} else {
			try {
				final int index = Integer.parseInt(args[1]);
				final Invite invite = invites.get(index - 1);
				invite.setResult(answer);

			} catch (NumberFormatException ex) {
				MessageUtils.sendMessage(player, "this-is-not-valid-number");
			}
		}
	}

	private void spectate(final Player player, final String[] args) {
		if (args.length == 2) {
			final String targetName = args[1];
			final Player target = Bukkit.getPlayerExact(targetName);

			if (target == null) {
				player.sendMessage("§cThis player is not online.");
				return;
			}

			final Member member = DataHandler.getMember(target.getUniqueId());
			if (member == null) {
				player.sendMessage("§cThis player is not in a duel.");
				return;
			}

			member.getTeam().getGame().spectate(player);

		} else {
			player.sendMessage("§cUsage: /duel spectate <player>");
		}
	}

	private void invite(final Player player, final String[] args) {
		final String targetName = args.length == 1 ? args[0] : args[1];
		final Player target = Bukkit.getPlayerExact(targetName);

		if (target == null) {
			MessageUtils.sendMessage(player, "target-is-not-online");
			return;
		}

		if (DataHandler.getMember(target.getUniqueId()) != null) {
			MessageUtils.sendMessage(player, "target-already-in-duel");
			return;
		}

		//1v1
		final GameBuilder gameBuilder = Game.create(plugin, player.getUniqueId()).setTeamAmount(2).setTeamSize(1).finishTime(60).totalRounds(1);

		new KitSelectionGUI(player, gameBuilder, selectedKit -> {
			gameBuilder.createTeam(player);

			gameBuilder.sendInvite(player, target, result -> {
				if (result.equals(InviteResult.ACCEPT)) {
					gameBuilder.createTeam(target);
					final Game game = gameBuilder.build();
					if (game == null) {
						MessageUtils.sendMessage(player, "no-arena-found");
						return;
					}
					game.startGame();
				}
			});
		}).open();


	}

}