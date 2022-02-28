package mc.obliviate.masterduels.invite;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.game.GameBuilder;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Invite {

	private static final Map<UUID, Invites> INVITES_MAP = new HashMap<>();
	private final GameBuilder gameBuilder;
	private final Player target;
	private final Player inviter;
	private final int expireTime;
	private final long invitedTime;
	private boolean expired;
	private Boolean answer = null;
	private InviteResponse response;

	public Invite(MasterDuels plugin, Player inviter, Player invited, GameBuilder gameBuilder) {
		this(plugin, inviter, invited, gameBuilder, plugin.getDatabaseHandler().getConfig().getInt("invite-timeout"));
	}

	public Invite(MasterDuels plugin, Player inviter, Player invited, GameBuilder gameBuilder, int expireTime) {
		this.gameBuilder = gameBuilder;
		this.expireTime = expireTime;
		this.target = invited;
		this.inviter = inviter;
		this.invitedTime = System.currentTimeMillis();

		if (inviter == null) {
			onExpire();
			Logger.error("An invite sent by null player!");
			return;
		}

		if (invited == null) {
			MessageUtils.sendMessage(inviter, "target-is-not-online");
			onExpire();
			return;
		}

		if (gameBuilder.getInvites().containsKey(invited.getUniqueId())) {
			MessageUtils.sendMessage(inviter, "invite.already-invited", new PlaceholderUtil().add("{target}", invited.getName()));
			onExpire();
			return;
		}

		if (!invited.isOnline()) {
			MessageUtils.sendMessage(inviter, "target-is-not-online");
			onExpire();
			return;
		}

		//todo cache invite receives
		if (!plugin.getSqlManager().getReceivesInvites(invited.getUniqueId())) {
			MessageUtils.sendMessage(inviter, "invite.toggle.you-can-not-invite", new PlaceholderUtil().add("{target}", invited.getName()));
			onExpire();
			return;
		}

		addInvite(invited.getUniqueId(), this);
		MessageUtils.sendMessage(inviter, "invite.target-has-invited", new PlaceholderUtil().add("{target}", target.getName()).add("{expire-time}", expireTime + ""));

		for (String inviteText : MessageUtils.getMessageConfig().getStringList("invite.you-invited")) {
			inviteText = inviteText + " ";
			inviteText = MessageUtils.applyPlaceholders(inviteText, new PlaceholderUtil().add("{inviter}", inviter.getName()).add("{expire-time}", expireTime + ""));
			inviteText = MessageUtils.parseColor(inviteText);

			if (inviteText.contains("{accept-button}") && inviteText.contains("{decline-button}")) {
				final TextComponent acceptButton = new TextComponent(MessageUtils.parseColor(MessageUtils.getMessage("invite.button.accept-button.text")));
				acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.parseColor(MessageUtils.getMessage("invite.button.accept-button.hover"))).create()));
				acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept"));

				final TextComponent declineButton = new TextComponent(MessageUtils.parseColor(MessageUtils.getMessage("invite.button.decline-button.text")));
				declineButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.parseColor(MessageUtils.getMessage("invite.button.decline-button.hover"))).create()));
				declineButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel decline"));

				final String[] strings = inviteText.split("\\{accept-button}|\\{decline-button}");

				target.spigot().sendMessage(new TextComponent(strings[0]), acceptButton, new TextComponent(strings[1]), declineButton, new TextComponent(strings[2]));
			} else {
				target.spigot().sendMessage(new TextComponent(inviteText));
			}
		}
		new BukkitRunnable() {
			public void run() {
				if (!expired && answer == null) {
					onExpire();
					MessageUtils.sendMessage(target, "invite.invite-expired-target", new PlaceholderUtil().add("{inviter}", inviter.getName()));
					MessageUtils.sendMessage(inviter, "invite.invite-expired-inviter", new PlaceholderUtil().add("{target}", target.getName()));
					response.onResponse(InviteResult.EXPIRE);
				}
			}
		}.runTaskLater(plugin, expireTime * 20L);


	}

	public static Invites findInvites(final Player player) {
		return INVITES_MAP.get(player.getUniqueId());
	}

	public static List<Invite> findInvites(final GameBuilder builder) {
		final List<Invite> inviteList = new ArrayList<>();
		for (Invites invites : INVITES_MAP.values()) {
			for (Invite invite : invites.getInvites()) {
				if (invite.getGameBuilder().equals(builder)) {
					inviteList.add(invite);
				}
			}
		}
		return inviteList;
	}

	private static void addInvite(final UUID uuid, final Invite invite) {
		Invites invites = INVITES_MAP.get(uuid);
		if (invites == null) {
			invites = new Invites(uuid);
		}
		invites.add(invite);
		INVITES_MAP.put(uuid, invites);
	}

	public int getExpireTime() {
		return this.expireTime;
	}

	public void onExpire() {
		expired = true;
		gameBuilder.removeInvite(target.getUniqueId());
	}

	public String getFormattedExpireTimeLeft() {
		long time = (invitedTime + (1000L * getExpireTime()) - System.currentTimeMillis()) / 1000;
		long minute;
		minute = TimeUnit.MINUTES.toHours(time);
		time -= (minute * 60);

		return minute + TimerUtils.MINUTE + time + TimerUtils.SECONDS;
	}

	public boolean isExpired() {
		return expired;
	}

	public GameBuilder getGameBuilder() {
		return gameBuilder;
	}

	public Player getInviter() {
		return inviter;
	}

	public Player getTarget() {
		return target;
	}

	public void setResult(boolean answer) {
		final Invites invites = INVITES_MAP.get(target.getUniqueId());
		if (invites.removeInvite(this)) {
			INVITES_MAP.remove(invites.getPlayerUniqueId());
		}
		expired = true;
		onExpire();
		this.answer = answer;

		//ACCEPT
		if (answer) {
			MessageUtils.sendMessage(getInviter(), "invite.target-accepted-the-invite", new PlaceholderUtil().add("{target}", target.getName()));
			final Player player = getTarget();
			MessageUtils.sendMessage(player, "invite.successfully-accepted", new PlaceholderUtil().add("{inviter}", target.getName()));
			response.onResponse(InviteResult.ACCEPT);
		}
		//DECLINE
		else {
			response.onResponse(InviteResult.DECLINE);
			MessageUtils.sendMessage(getInviter(), "invite.target-declined-the-invite", new PlaceholderUtil().add("{target}", target.getName()));
			MessageUtils.sendMessage(getTarget(), "invite.successfully-declined", new PlaceholderUtil().add("{inviter}", inviter.getName()));

		}

	}

	public InviteResponse getResponse() {
		return response;
	}

	public void onResponse(InviteResponse response) {
		this.response = response;
	}
}
