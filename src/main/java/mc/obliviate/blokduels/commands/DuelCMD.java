package mc.obliviate.blokduels.commands;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.arena.Arena;
import mc.obliviate.blokduels.data.DataHandler;
import mc.obliviate.blokduels.game.Game;
import mc.obliviate.blokduels.game.GameBuilder;
import mc.obliviate.blokduels.invite.Invite;
import mc.obliviate.blokduels.invite.InviteResult;
import mc.obliviate.blokduels.invite.Invites;
import mc.obliviate.blokduels.playerduelsetup.selectduelarena.SelectDuelArenaGUI;
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

		if (DataHandler.getMember(player.getUniqueId()) != null) {
			player.sendMessage("§cYou are already in a duel.");
			return false;
		}

		if (args.length == 0) {
			player.sendMessage("§cUsage: /duel <player>");
			return false;
		}

		if (args[0].equalsIgnoreCase("accept")) {
			answerInvite(player, true, args);
			return true;
		} else if (args[0].equalsIgnoreCase("decline")) {
			answerInvite(player, false, args);
			return true;
		} else if (args[0].equalsIgnoreCase("setup")) {
			new SelectDuelArenaGUI(player).open();
			return true;
		}

		final Player target = Bukkit.getPlayerExact(args[0]);

		if (target == null) {
			player.sendMessage("§cThis player is not online.");
			return false;
		}

		if (DataHandler.getMember(target.getUniqueId()) != null) {
			player.sendMessage("§cThis player is already in a duel.");
			return false;
		}

		//1v1
		final Arena arena = Arena.findArena(1, 2);

		if (arena == null) {
			player.sendMessage("§cCould not found any available arena.");
			return false;
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

}
