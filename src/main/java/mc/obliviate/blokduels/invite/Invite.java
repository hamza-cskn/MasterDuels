package mc.obliviate.blokduels.invite;

import mc.obliviate.blokduels.BlokDuels;
import mc.obliviate.blokduels.game.GameBuilder;
import mc.obliviate.blokduels.utils.MessageUtils;
import mc.obliviate.blokduels.utils.placeholder.PlaceholderUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Invite {

	private static final Map<UUID, Invites> playerInvites = new HashMap<>();
	private final GameBuilder gameBuilder;
	private final Player target;
	private final Player inviter;
	private final int expireTime;
	private final long invitedTime;
	private final BlokDuels plugin;
	private boolean expired;
	private Boolean answer = null;
	private InviteResponse response;

	public Invite(BlokDuels plugin, Player inviter, Player invited, GameBuilder gameBuilder) {
		this(plugin, inviter, invited, gameBuilder, 120);
	}

	public Invite(BlokDuels plugin, Player inviter, Player invited, GameBuilder gameBuilder, int expireTime) {
		this.plugin = plugin;
		this.gameBuilder = gameBuilder;
		this.expireTime = expireTime;
		this.target = invited;
		this.inviter = inviter;
		this.invitedTime = System.currentTimeMillis();

		if (gameBuilder.getInvites().containsKey(invited.getUniqueId())) {
			MessageUtils.sendMessage(inviter, "invite.already-invited", new PlaceholderUtil().add("{inviter}", invited.getName()));
			expired = true;
			onExpire();
			return;
		}

		if (!invited.isOnline()) {
			MessageUtils.sendMessage(inviter, "target-is-not-online");
			expired = true;
			onExpire();
			return;
		}
		addInvite(invited.getUniqueId(), this);
		MessageUtils.sendMessage(inviter, "invite.target-has-invited", new PlaceholderUtil().add("{target}", target.getName()).add("{expire_time}", expireTime + ""));
		MessageUtils.sendMessage(target, "invite.you-invited", new PlaceholderUtil().add("{inviter}", target.getName()).add("{expire_time}", expireTime + ""));

		final TextComponent acceptButton = new TextComponent(MessageUtils.parseColor(MessageUtils.getMessage("invite.accept-button")));
		acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.parseColor(MessageUtils.getMessage("invite.accept-button-name"))).create()));
		acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept"));

		final TextComponent declineButton = new TextComponent(MessageUtils.parseColor(MessageUtils.getMessage("invite.decline-button")));
		declineButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageUtils.parseColor(MessageUtils.getMessage("invite.decline-button-name"))).create()));
		declineButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel decline"));

		target.spigot().sendMessage(acceptButton, declineButton);

		expired = false;
		new BukkitRunnable() {
			public void run() {
				if (!expired && answer == null) {
					expired = true;
					onExpire();
					MessageUtils.sendMessage(target, "invite.invite-expired-target", new PlaceholderUtil().add("{inviter}", inviter.getName()));
					MessageUtils.sendMessage(inviter, "invite.invite-expired-inviter", new PlaceholderUtil().add("{target}", target.getName()));
					response.onResponse(InviteResult.EXPIRE);
				}
			}
		}.runTaskLater(plugin, expireTime * 20L);


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

	public int getExpireTime() {
		return this.expireTime;
	}

	private void onExpire() {
		gameBuilder.removeInvite(target.getUniqueId());
	}

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

	public GameBuilder getTeamBuilder() {
		return gameBuilder;
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
			MessageUtils.sendMessage(getInviter(), "invite.target-accepted-the-invite", new PlaceholderUtil().add("{target}", target.getName()));
			final Player player = getTarget();
			MessageUtils.sendMessage(player, "invite.successfully-accepted", new PlaceholderUtil().add("{inviter}", target.getName()));
			/*if (teamBuilder.getTeamSize() < ConfigHandler.getMemberLimitPerClan()) {
				teamBuilder.addMember(getTarget());
			} else {
				MessageUtils.sendMessage(getTarget(),"invite.clan-is-full");
			}
			 */
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
