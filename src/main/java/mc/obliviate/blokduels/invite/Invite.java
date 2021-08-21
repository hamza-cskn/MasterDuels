package mc.obliviate.blokduels.invite;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.game.TeamBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Invite {


	private static final HashMap<UUID, Invites> playerInvites = new HashMap<>();

	private boolean expired;
	private final TeamBuilder teamBuilder;
	private final Player target;
	private final Player inviter;
	private Boolean answer = null;
	private final int expireTime;
	private final long invitedTime;
	private final BlokDuels plugin;

	public Invite(BlokDuels plugin, Player inviter, Player invited, TeamBuilder teamBuilder) {
		this(plugin, inviter, invited, teamBuilder, 120);
	}

	public Invite(BlokDuels plugin, Player inviter, Player invited, TeamBuilder teamBuilder, int expireTime) {


		this.plugin = plugin;
		this.teamBuilder = teamBuilder;
		this.expireTime = expireTime;
		this.target = invited;
		this.inviter = inviter;
		this.invitedTime = System.currentTimeMillis();

		if (teamBuilder.getInvites().containsKey(invited.getUniqueId())) {
			inviter.sendMessage(MessageUtils.getMessage("invite.already-invited")
					.replace("{inviter}", invited.getName()));
			expired = true;
			return;
		}

		if (!invited.isOnline()) {
			inviter.sendMessage(MessageUtils.getMessage("target-is-not-online"));
			expired = true;
			onExpire();
			return;
		}
		addInvite(invited.getUniqueId(), this);
		inviter.sendMessage(MessageUtils.getMessage("invite.target-has-invited")
				.replace("{target}", target.getName())
				.replace("{expire_time}", expireTime + ""));
		target.sendMessage(MessageUtils.getMessage("invite.you-invited")
				.replace("{inviter}", inviter.getName())
				.replace("{expire_time}", expireTime + ""));

		TextComponent button = new TextComponent(MessageUtils.getMessage("invite.accept-button"));
		button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kabile kabulet"));
		TextComponent button2 = new TextComponent(MessageUtils.getMessage("invite.decline-button"));
		button2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kabile reddet"));

		target.spigot().sendMessage(button, button2);

		expired = false;
		new BukkitRunnable() {
			public void run() {
				if (!expired && answer == null) {
					expired = true;
					onExpire();
					target.sendMessage(MessageUtils.getMessage("invite.invite-expired-target")
							.replace("{inviter}", inviter.getName()));
					inviter.sendMessage(MessageUtils.getMessage("invite.invite-expired-inviter")
							.replace("{target}", target.getName()));
				}
			}
		}.runTaskLater(plugin, expireTime * 20L);


	}

	public int getExpireTime() {
		return this.expireTime;
	}

	private void onExpire() {
		teamBuilder.removeInvite(target.getUniqueId());
	}

	@Deprecated
	public String getFormattedExpireTimeLeft() {
		long time = (invitedTime + (1000L * getExpireTime()) - System.currentTimeMillis()) / 1000;
		long minute;
		minute = TimeUnit.MINUTES.toHours(time);
		time -= (minute * 60);


		return minute + "dk " + time + "sn";
	}

	public boolean isExpired() {
		return expired;
	}

	public TeamBuilder getTeamBuilder() {
		return teamBuilder;
	}

	public Player getInviter() {
		return inviter;
	}

	public Player getTarget() {
		return target;
	}


	public void setResult(boolean answer) {
		final Invites invites = playerInvites.get(target.getUniqueId());
		if (invites.removeInvite(this)) {
			playerInvites.remove(invites.getPlayerUniqueId());
		}
		expired = true;
		onExpire();
		this.answer = answer;

		//ACCEPT
		if (answer) {
			getInviter().sendMessage(MessageUtils.getMessage("invite.target-accepted-the-invite").replace("{target}",target.getName()));
			Player player = getTarget();
			player.sendMessage(MessageUtils.getMessage("invite.successfully-accepted").replace("{inviter}",target.getName()));
			if (teamBuilder.getTeamSize() < ConfigHandler.getMemberLimitPerClan()) {
				teamBuilder.addMember(getTarget());
			} else {
				getTarget().sendMessage(MessageUtils.getMessage("invite.clan-is-full"));
			}
		}
		//DECLINE
		else {
			getInviter().sendMessage(MessageUtils.getMessage("invite.target-declined-the-invite").replace("{target}", target.getName()));
			getTarget().sendMessage(MessageUtils.getMessage("invite.successfully-declined").replace("{inviter}", inviter.getName()));

		}

	}

	public static Invites findInvites(final Player player) {
		return playerInvites.get(player.getUniqueId());

	}

	private static void addInvite(final UUID uuid, final Invite invite) {
		Invites invites = playerInvites.get(uuid);
		if (invites == null) {
			invites = new Invites(uuid);
		}
		invites.add(invite);
		playerInvites.put(uuid, invites);
	}


}
