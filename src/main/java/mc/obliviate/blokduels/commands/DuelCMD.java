package mc.obliviate.blokduels.commands;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameBuilder;
import mc.obliviate.blokduels.invite.Invite;
import mc.obliviate.blokduels.invite.InviteResult;
import mc.obliviate.blokduels.invite.Invites;
import mc.obliviate.blokduels.team.Member;
import mc.obliviate.blokduels.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCMD implements CommandExecutor {

	private final BlokDuels plugin;

	public DuelCMD(BlokDuels plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if (!(sender instanceof Player)) return false;

		final Player player = ((Player) sender).getPlayer();

		final Member member = DataHandler.getMember(player.getUniqueId());

		if (args.length == 0) {
			player.sendMessage("§c§lUSAGE");
			player.sendMessage("§7/duel <player>");
			player.sendMessage("§7/duel invite <player>");
			player.sendMessage("§7/duel leave");
			player.sendMessage("§7/duel accept");
			player.sendMessage("§7/duel decline");
			return false;
		}

		if (args[0].equalsIgnoreCase("leave")) {
			member.getTeam().getGame().leaveMember(member);
			return true;
		}

		//THESE COMMANDS BLOCKED FOR PLAYERS WHO IN DUEL

		if (member != null) {
			player.sendMessage("§cYou are in a duel game already.");
			return false;
		}

		if (args[0].equalsIgnoreCase("accept")) {
			answerInvite(player, true, args);
			return true;
		} else if (args[0].equalsIgnoreCase("decline")) {
			answerInvite(player, false, args);
			return true;
		} else if (args[0].equalsIgnoreCase("spectate")) {
			spectate(player, args);

		} else if (args.length == 1 || args[0].equalsIgnoreCase("invite")) {
			invite(player, args);
		}
		return true;

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

			member.getTeam().getGame().getSpectatorData().spectate(player);

		} else {
			player.sendMessage("§cUsage: /duel spectate <player>");
		}
	}

	private void invite(final Player player, final String[] args) {
		final String targetName = args.length == 1 ? args[0] : args[1];
		final Player target = Bukkit.getPlayerExact(targetName);

		if (target == null) {
			player.sendMessage("§cThis player is not online.");
			return;
		}

		if (DataHandler.getMember(target.getUniqueId()) != null) {
			player.sendMessage("§cThis player is already in a duel.");
			return;
		}

		//1v1
		final Arena arena = Arena.findArena(1, 2);

		if (arena == null) {
			player.sendMessage("§cCould not found any available arena.");
			return;
		}

		final GameBuilder gameBuilder = Game.create(plugin, arena).teamAmount(2).teamSize(1).finishTime(60).totalRounds(1);

		gameBuilder.createTeam(player);

		gameBuilder.sendInvite(player, target, result -> {
			if (result.equals(InviteResult.ACCEPT)) {
				gameBuilder.createTeam(target);
				final Game game = gameBuilder.build();
				if (game == null) {
					target.sendMessage("arena already started");
					return;
				}
				game.startGame();
			}
		});
	}

}
