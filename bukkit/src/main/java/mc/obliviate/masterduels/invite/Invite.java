package mc.obliviate.masterduels.invite;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.invite.InviteResult;
import mc.obliviate.masterduels.api.user.IUser;
import mc.obliviate.masterduels.data.DataHandler;
import mc.obliviate.masterduels.game.GameCreator;
import mc.obliviate.masterduels.user.team.Member;
import mc.obliviate.masterduels.utils.Logger;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Invite {

	private static final Map<UUID, Invites> INVITES_MAP = new HashMap<>();
	private final GameCreator gameCreator;
	private final Player target;
	private final Player inviter;
	private final int expireTime;
	private final long invitedTime;
	private boolean expired;
	private Boolean answer = null;
	private Consumer<InviteResult> response;

	public Invite(MasterDuels plugin, Player inviter, Player invited, GameCreator gameCreator) {
		this(plugin, inviter, invited, gameCreator, plugin.getDatabaseHandler().getConfig().getInt("invite-timeout"));
	}

	public Invite(MasterDuels plugin, Player inviter, Player invited, GameCreator gameCreator, int expireTime) {
		this.gameCreator = gameCreator;
		this.expireTime = expireTime;
		this.target = invited;
		this.inviter = inviter;
		this.invitedTime = System.currentTimeMillis();

		//check: inviter is not null
		if (inviter == null) {
			onExpire();
			Logger.error("An invite sent by null player!");
			return;
		}

		//check: invited is not null
		if (invited == null) {
			MessageUtils.sendMessage(inviter, "target-is-not-online");
			onExpire();
			return;
		}

		//check: inviter is online definitely
		if (!invited.isOnline()) {
			MessageUtils.sendMessage(inviter, "target-is-not-online");
			onExpire();
			return;
		}

		//check: invited is not in a duel game
		final IUser invitedUser = DataHandler.getUser(invited.getUniqueId());

		if (invitedUser instanceof Member) {
			MessageUtils.sendMessage(inviter, "target-already-in-duel", new PlaceholderUtil().add("{target}", inviter.getName()));
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

		if (gameCreator == null) {
			InviteUtils.sendInviteMessage(this, MessageUtils.getMessageConfig().getStringList("invite.normal-invite-text"));
		} else {
			InviteUtils.sendInviteMessage(this, MessageUtils.getMessageConfig().getStringList("invite.game-creator-invite-text"));
		}
		new BukkitRunnable() {
			public void run() {
				if (!expired && answer == null) {
					onExpire();
					MessageUtils.sendMessage(target, "invite.invite-expired-target", new PlaceholderUtil().add("{inviter}", inviter.getName()));
					MessageUtils.sendMessage(inviter, "invite.invite-expired-inviter", new PlaceholderUtil().add("{target}", target.getName()));
					response.accept(InviteResult.EXPIRE);
				}
			}
		}.runTaskLater(plugin, expireTime * 20L);


	}

	public static Invites findInvites(final Player player) {
		return INVITES_MAP.get(player.getUniqueId());
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
		if (gameCreator != null) gameCreator.removeInvite(target.getUniqueId());
	}

	public String getFormattedExpireTimeLeft() {
		return TimerUtils.formatTimeAsTime((invitedTime + (1000L * expireTime) - System.currentTimeMillis()));
	}

	public boolean isExpired() {
		return expired;
	}

	public GameCreator getGameCreator() {
		return gameCreator;
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
		onExpire();
		this.answer = answer;

		//ACCEPT
		if (answer) {
			MessageUtils.sendMessage(getInviter(), "invite.target-accepted-the-invite", new PlaceholderUtil().add("{target}", target.getName()));
			final Player player = getTarget();
			MessageUtils.sendMessage(player, "invite.successfully-accepted", new PlaceholderUtil().add("{inviter}", target.getName()));
			response.accept(InviteResult.ACCEPT);
		}
		//DECLINE
		else {
			response.accept(InviteResult.DECLINE);
			MessageUtils.sendMessage(getInviter(), "invite.target-declined-the-invite", new PlaceholderUtil().add("{target}", target.getName()));
			MessageUtils.sendMessage(getTarget(), "invite.successfully-declined", new PlaceholderUtil().add("{inviter}", inviter.getName()));

		}

	}

	public Consumer<InviteResult> getResponse() {
		return response;
	}

	public void onResponse(Consumer<InviteResult> response) {
		this.response = response;
	}
}