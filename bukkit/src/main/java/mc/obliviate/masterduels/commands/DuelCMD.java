package mc.obliviate.masterduels.commands;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.game.creator.MatchCreator;
import mc.obliviate.masterduels.gui.DuelArenaListGUI;
import mc.obliviate.masterduels.gui.DuelHistoryLogGUI;
import mc.obliviate.masterduels.gui.creator.DuelMatchCreatorGUI;
import mc.obliviate.masterduels.gui.creator.DuelMatchCreatorNonOwnerGUI;
import mc.obliviate.masterduels.invite.Invite;
import mc.obliviate.masterduels.invite.InviteRecipient;
import mc.obliviate.masterduels.invite.InviteUtils;
import mc.obliviate.masterduels.kit.gui.KitSelectionGUI;
import mc.obliviate.masterduels.queue.DuelQueue;
import mc.obliviate.masterduels.queue.DuelQueueHandler;
import mc.obliviate.masterduels.queue.DuelQueueTemplate;
import mc.obliviate.masterduels.queue.gui.DuelQueueListGUI;
import mc.obliviate.masterduels.statistics.DuelStatistic;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.Spectator;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
		final IUser user = UserHandler.getUser(player.getUniqueId());

		if (args.length == 0) {
			MessageUtils.sendMessage(player, "duel-command.usage", new PlaceholderUtil().add("{build-version}", plugin.getDescription().getVersion()));
			return false;
		}

		if (args[0].equalsIgnoreCase("history")) {
			new DuelHistoryLogGUI(player).open();
			return true;
		} else if (args[0].equalsIgnoreCase("leave")) {
			if (user instanceof Member) {
				final Member member = ((Member) user);
				member.getMatch().getMatchState().leave(member); //todo add methods of game states to game.class
				return true;
			} else if (user instanceof Spectator) {
				final Spectator spectator = ((Spectator) user);
				spectator.getMatch().getMatchState().leave(spectator);
				return true;
			} else {
				MessageUtils.sendMessage(player, "you-are-not-in-duel");
				return false;
			}
		} else if (args[0].equalsIgnoreCase("top")) {
			top(player, Arrays.asList(args));
			return true;
		} else if (args[0].equalsIgnoreCase("toggle")) {
			final boolean state = plugin.getSqlManager().toggleReceivesInvites(player.getUniqueId());
			if (state) {
				MessageUtils.sendMessage(player, "invite.toggle.turned-on");
			} else {
				MessageUtils.sendMessage(player, "invite.toggle.turned-off");
			}
			return true;
		} else if (args[0].equalsIgnoreCase("stats")) {
			stats(player, args);
			return true;
		}

		//COMMANDS BELOW ARE BLOCKED FOR PLAYERS WHO IN DUEL

		if (user instanceof Member) {
			MessageUtils.sendMessage(player, "you-are-in-duel");
			return false;
		}

		if (ConfigurationHandler.getMenus().getBoolean("duel-arenas-gui.enabled") && args[0].equalsIgnoreCase("arenas")) {
			new DuelArenaListGUI(player).open();
			return true;
		} else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("decline")) {
			responseInvite(player, args);
			return true;
		} else if (args[0].equalsIgnoreCase("spectate")) {
			spectate(player, args);
		} else if (args[0].equalsIgnoreCase("queue") && DuelQueueHandler.enabled) {
			queue(player, Arrays.asList(args));
		} else if (args[0].equalsIgnoreCase("creator")) {
			creator(player, Arrays.asList(args));
		} else if (args.length == 1 || args[0].equalsIgnoreCase("invite")) {
			invite(player, args);
		}
		return true;
	}

	private void creator(final Player player, List<String> args) {
		if (DuelQueue.findQueueOfPlayer(player) == null) {
			MatchCreator matchCreator = MatchCreator.getCreator(player.getUniqueId());
			if (matchCreator == null) {
				matchCreator = new MatchCreator(player.getUniqueId());
			}
			if (matchCreator.getOwnerPlayer().equals(player.getUniqueId())) {
				new DuelMatchCreatorGUI(player, matchCreator).open();
			} else {
				new DuelMatchCreatorNonOwnerGUI(player, matchCreator).open();
			}
		} else {
			MessageUtils.sendMessage(player, "queue.you-are-in-queue");
		}
	}

	private void queue(final Player player, List<String> args) {
		if (args.size() == 1) {
			MessageUtils.sendMessage(player, "queue.usage");
			return;
		}
		if (args.get(1).equalsIgnoreCase("menu") || args.get(1).equalsIgnoreCase("gui")) {
			new DuelQueueListGUI(player).open();
		} else if (args.get(1).equalsIgnoreCase("join")) {
			if (args.size() == 2) {
				MessageUtils.sendMessage(player, "queue.no-queue-name");
				return;
			}

			final DuelQueue queue = DuelQueue.getAvailableQueues().get(DuelQueueTemplate.getQueueTemplateFromName(args.get(2)));
			if (queue == null) {
				MessageUtils.sendMessage(player, "queue.queue-not-found", new PlaceholderUtil().add("{entry}", args.get(2)));
				return;
			}

			DuelQueue currentQueue = DuelQueue.findQueueOfPlayer(player);
			if (currentQueue != null) {
				if (currentQueue.equals(queue)) return;
				if (!currentQueue.isLocked()) {
					currentQueue.removePlayer(player);
				}
			}

			queue.addPlayer(player);
			MessageUtils.sendMessage(player, "queue.joined",
					new PlaceholderUtil()
							.add("{queue-name}", queue.getName())
							.add("{player-amount}", queue.getBuilder().getPlayers().size() + "")
							.add("{max-player-amount}", queue.getBuilder().getTeamAmount() * queue.getBuilder().getTeamSize() + "")
			);
		} else if (args.get(1).equalsIgnoreCase("leave")) {
			final DuelQueue queue = DuelQueue.findQueueOfPlayer(player);
			if (queue == null) {
				MessageUtils.sendMessage(player, "queue.you-are-not-in-queue");
				return;
			}
			queue.removePlayer(player);
			MessageUtils.sendMessage(player, "queue.left", new PlaceholderUtil().add("{queue-name}", queue.getName()));
		}
	}

	private void top(final Player player, List<String> args) {
		final LinkedList<DuelStatistic> statistics;

		final ConfigurationSection section = ConfigurationHandler.getConfig().getConfigurationSection("top.top-wins");
		final String nobodyText = MessageUtils.parseColor(MessageUtils.getMessage("top.nobody"));
		if (section == null) return;

		final int limit = section.getInt("calculation-limit", 10);
		statistics = plugin.getSqlManager().getTopPlayers("wins", Math.max(limit, 1));

		final PlaceholderUtil placeholderUtil = new PlaceholderUtil();
		for (int index = 1; index <= limit; index++) {

			if (statistics.size() < index) {
				placeholderUtil.add("{top-" + index + "-name}", nobodyText);
				placeholderUtil.add("{top-" + index + "-wins}", 0 + "");
				placeholderUtil.add("{top-" + index + "-losses}", 0 + "");
				placeholderUtil.add("{top-" + index + "-gamesplayed}", 0 + "");
			} else {
				final DuelStatistic stat = statistics.get(index - 1);
				placeholderUtil.add("{top-" + index + "-name}", Bukkit.getOfflinePlayer(stat.getPlayerUniqueId()).getName() + "");
				placeholderUtil.add("{top-" + index + "-wins}", stat.getWins() + "");
				placeholderUtil.add("{top-" + index + "-losses}", stat.getLosses() + "");
				placeholderUtil.add("{top-" + index + "-gamesplayed}", (stat.getWins() + stat.getLosses()) + "");
			}
		}

		MessageUtils.sendMessage(player, "top.top-wins.message", placeholderUtil);
	}

	private void stats(final Player player, final String[] args) {
		final UUID target = args.length == 1 ? player.getUniqueId() : Bukkit.getOfflinePlayer(args[1]).getUniqueId();
		final DuelStatistic statistic = plugin.getSqlManager().getStatistic(target);
		MessageUtils.sendMessage(player, "statistics",
				new PlaceholderUtil()
						.add("{wins}", statistic.getWins() + "").add("{loses}", statistic.getLosses() + ""));

	}

	private void responseInvite(Player player, String[] args) {
		final InviteRecipient receiver = InviteRecipient.getInviteRecipient(player.getUniqueId());

		final Invite.InviteState responseState = args[0].equalsIgnoreCase("accept") ? Invite.InviteState.ACCEPTED : Invite.InviteState.REJECTED;

		if (receiver.getInvites().size() == 0) {
			MessageUtils.sendMessage(player, "invite.you-dont-have-invite");
			return;

		}

		if (receiver.getInvites().size() == 1) {
			receiver.getInvites().get(0).response(responseState);
			return;
		}

		if (args.length >= 2) {
			int inviteNo;
			try {
				inviteNo = Integer.parseInt(args[1]);
			} catch (NumberFormatException exception) {
				MessageUtils.sendMessage(player, "this-is-not-valid-number");
				return;
			}

			receiver.getInvites().get(inviteNo - 1).response(responseState);

		} else {
			int index = 0;
			for (final Invite invite : receiver.getInvites()) {
				player.sendMessage(MessageUtils.parseColor("&8- &f/duel " + args[0] + " &7" + index++ + " -> " + Bukkit.getOfflinePlayer(invite.getSenderUniqueId()).getName() + " (" + TimerUtils.formatTimeUntilThenAsTime(invite.getExpireOutTime()) + ")"));
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

			final Member member = UserHandler.getMember(target.getUniqueId());
			if (member == null) {
				player.sendMessage("§cThis player is not in a duel.");
				return;
			}

			member.getTeam().getMatch().getGameSpectatorManager().spectate(player);

		} else {
			player.sendMessage("§cUsage: /duel spectate <player>");
		}
	}

	private void invite(final Player player, final String[] args) {
		final String targetName = args.length == 1 ? args[0] : args[1];
		final Player target = Bukkit.getPlayerExact(targetName);

		//check: target is online
		if (target == null) {
			MessageUtils.sendMessage(player, "target-is-not-online");
			return;
		}

		//check: target is not sender
		if (player.equals(target)) {
			MessageUtils.sendMessage(player, "invite.you-cannot-invite-yourself");
			return;
		}

		//check: target is not in duel
		if (UserHandler.getMember(target.getUniqueId()) != null) {
			MessageUtils.sendMessage(player, "target-already-in-duel", new PlaceholderUtil().add("{target}", target.getName()));
			return;
		}

		//check: target accepts invites
		if (!plugin.getSqlManager().getReceivesInvites(target.getUniqueId())) {
			MessageUtils.sendMessage(player, "invite.toggle.you-can-not-invite", new PlaceholderUtil().add("{target}", target.getName()));
			return;
		}
		//1v1
		final MatchBuilder matchBuilder = Match.create();
		matchBuilder.setTeamsAttributes(1, 2).setDuration(Duration.ofMinutes(1)).setTotalRounds(1);

		new KitSelectionGUI(player, matchBuilder, selectedKit -> {
			Invite.InviteBuildResult buildResult = Invite.create()
					.setExpireTimeLater(ConfigurationHandler.getConfig().getInt("invite-timeout") * 1000L)
					.setSender(player.getUniqueId())
					.setReceiver(target.getUniqueId())
					.onResponse(invite -> {
						switch (invite.getState()) {
							case ACCEPTED:
								MessageUtils.sendMessage(target, "invite.normal-invite.successfully-accepted", new PlaceholderUtil().add("{inviter}", Utils.getDisplayName(player)));
								MessageUtils.sendMessage(player, "invite.normal-invite.target-accepted-the-invite", new PlaceholderUtil().add("{target}", Utils.getDisplayName(target)));
								break;
							case REJECTED:
								MessageUtils.sendMessage(target, "invite.normal-invite.successfully-declined", new PlaceholderUtil().add("{inviter}", Utils.getDisplayName(player)));
								MessageUtils.sendMessage(player, "invite.normal-invite.target-declined-the-invite", new PlaceholderUtil().add("{target}", Utils.getDisplayName(target)));
								break;
							case EXPIRED:
								MessageUtils.sendMessage(target, "invite.normal-invite.invite-expired-target", new PlaceholderUtil().add("{inviter}", Utils.getDisplayName(player)));
								MessageUtils.sendMessage(player, "invite.normal-invite.invite-expired-inviter", new PlaceholderUtil().add("{target}", Utils.getDisplayName(target)));
								break;
						}
						if (invite.getState().equals(Invite.InviteState.ACCEPTED)) {
							matchBuilder.addPlayer(target, selectedKit);

							Match game = matchBuilder.build();
							if (game == null) {
								MessageUtils.sendMessage(player, "no-arena-found");
								return;
							}
							game.start();
						} else {
							matchBuilder.destroy();
						}
					}).build();

			if (buildResult.getInviteBuildState().equals(Invite.InviteBuildState.ERROR_ALREADY_INVITED)) {
				MessageUtils.sendMessage(player, "invite.already-invited", new PlaceholderUtil().add("{target}", target.getName()));

			} else if (buildResult.getInviteBuildState().equals(Invite.InviteBuildState.SUCCESS)) {
				MessageUtils.sendMessage(player, "invite.normal-invite.target-has-invited", new PlaceholderUtil().add("{target}", target.getName()).add("{expire-time}", TimerUtils.formatTimeUntilThenAsTimer(buildResult.getInvite().getExpireOutTime()) + ""));
				InviteUtils.sendInviteMessage(buildResult.getInvite(), MessageUtils.getMessageConfig().getConfigurationSection("invite.normal-invite"));

				matchBuilder.addPlayer(player, selectedKit);
			}
		}).open();


	}

}
