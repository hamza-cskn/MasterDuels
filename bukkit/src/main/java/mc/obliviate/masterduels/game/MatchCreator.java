package mc.obliviate.masterduels.game;

import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.invite.Invite;
import mc.obliviate.masterduels.invite.InviteUtils;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.Utils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

/**
 * Game Creator classes are player based game builders.
 * they store and manages invites, owners additional.
 */
public class MatchCreator {

	//player uuid, game creator
	public static final Map<UUID, MatchCreator> GAME_CREATOR_MAP = new HashMap<>();
	public static final List<Kit> ALLOWED_KITS = new ArrayList<>();
	public static final List<GameRule> ALLOWED_GAME_RULES = new ArrayList<>();
	public static int MAX_TEAM_AMOUNT;
	public static int MIN_TEAM_AMOUNT;
	public static int MAX_TEAM_SIZE;
	public static int MIN_TEAM_SIZE;
	public static int MAX_GAME_TIME;
	public static int MIN_GAME_TIME;
	public static int MAX_ROUNDS;
	public static int MIN_ROUNDS;

	//invited player's uuid, invite
	private final Map<UUID, Invite> invites = new HashMap<>();
	private final UUID ownerPlayer;
	private final MatchBuilder builder;

	public MatchCreator(UUID ownerPlayer) {
		this.ownerPlayer = ownerPlayer;
		this.builder = Match.create().setTeamsAttributes(1, 2).setDuration(Duration.ofMinutes(5)).setTotalRounds(1).setTotalRounds(1);

		//check: player uuid is not null
		if (ownerPlayer == null) {
			destroy();
			return;
		}

		//check: are player online and uuid, valid.
		final Player player = Bukkit.getPlayer(ownerPlayer);
		if (player == null) {
			destroy();
			return;
		}

		//check: is game creator already exist
		final MatchCreator matchCreator = getCreator(ownerPlayer);
		if (matchCreator != null) {
			matchCreator.destroy();
		}

		builder.addPlayer(player);
		GAME_CREATOR_MAP.put(ownerPlayer, this);
	}

	public Map<UUID, Invite> getInvites() {
		return invites;
	}

	public void trySendInvite(final Player sender, final Player target, final Consumer<Invite> response) {

		//check: target is online
		if (target == null) {
			MessageUtils.sendMessage(sender, "target-is-not-online");
			return;
		}

		//check: target is not sender
		if (sender.equals(target)) {
			MessageUtils.sendMessage(sender, "invite.you-cannot-invite-yourself");
			return;
		}

		IUser user = UserHandler.getUser(target.getUniqueId());
		//check: target is not in duel
		if (user instanceof Member) {
			MessageUtils.sendMessage(sender, "target-already-in-duel");
			return;
		}

		//check: target accepts invites
		if (!user.inviteReceiving()) {
			MessageUtils.sendMessage(sender, "invite.toggle.you-can-not-invite", new PlaceholderUtil().add("{target}", target.getName()));
			return;
		}

		//check: target is already in
		if (builder.getPlayers().contains(target.getUniqueId())) {
			MessageUtils.sendMessage(sender, "invite.game-creator-invite.player-already-in-creator", new PlaceholderUtil().add("{target}", target.getName()));
			return;
		}

		final Invite.InviteBuildResult buildResult = Invite.create()
				.setExpireTimeLater(ConfigurationHandler.getConfig().getInt("invite-timeout") * 1000L)
				.setReceiver(target.getUniqueId())
				.setSender(ownerPlayer)
				.onResponse(invite -> {
					this.removeInvite(invite.getRecipientUniqueId());
					switch (invite.getState()) {
						case ACCEPTED:
							MessageUtils.sendMessage(target, "invite.game-creator-invite.successfully-accepted", new PlaceholderUtil().add("{inviter}", Utils.getDisplayName(target)));
							MessageUtils.sendMessage(sender, "invite.game-creator-invite.target-accepted-the-invite", new PlaceholderUtil().add("{target}", Utils.getDisplayName(sender)));
							break;
						case REJECTED:
							MessageUtils.sendMessage(target, "invite.game-creator-invite.successfully-declined", new PlaceholderUtil().add("{inviter}", Utils.getDisplayName(target)));
							MessageUtils.sendMessage(sender, "invite.game-creator-invite.target-declined-the-invite", new PlaceholderUtil().add("{target}", Utils.getDisplayName(sender)));
							break;
						case EXPIRED:
							MessageUtils.sendMessage(target, "invite.game-creator-invite.invite-expired-target", new PlaceholderUtil().add("{inviter}", Utils.getDisplayName(target)));
							MessageUtils.sendMessage(sender, "invite.game-creator-invite.invite-expired-inviter", new PlaceholderUtil().add("{target}", Utils.getDisplayName(sender)));
							break;
					}

					final MatchCreator creator = getCreator(target.getUniqueId());
					if (creator != null) creator.destroy();
					if (builder.getData().getGameTeamManager().areAllTeamsFull()) {
						MessageUtils.sendMessage(target, "invite.game-creator-invite.all-teams-are-full-target");
						MessageUtils.sendMessage(sender, "invite.game-creator-invite.all-teams-are-full-inviter");
						return;
					}

					response.accept(invite);
				}).build();

		if (buildResult.getInviteBuildState().equals(Invite.InviteBuildState.ERROR_ALREADY_INVITED)) {
			MessageUtils.sendMessage(sender, "invite.already-invited", new PlaceholderUtil().add("{target}", target.getName()));
			return;
		}


		if (!buildResult.getInviteBuildState().equals(Invite.InviteBuildState.SUCCESS)) return;

		MessageUtils.sendMessage(sender, "invite.game-creator-invite.target-has-invited", new PlaceholderUtil().add("{target}", target.getName()).add("{expire-time}", TimerUtils.formatTimeUntilThenAsTimer(buildResult.getInvite().getExpireOutTime()) + ""));
		InviteUtils.sendInviteMessage(buildResult.getInvite(), MessageUtils.getMessageConfig().getConfigurationSection("invite.game-creator-invite"));
		invites.put(target.getUniqueId(), buildResult.getInvite());

	}

	public void removeInvite(final UUID uuid) {
		invites.remove(uuid);
	}

	public MatchBuilder getBuilder() {
		return builder;
	}

	public UUID getOwnerPlayer() {
		return ownerPlayer;
	}

	public void destroy() {
		GAME_CREATOR_MAP.remove(ownerPlayer);
		for (final Invite invite : invites.values()) {
			invite.response(Invite.InviteState.CANCELLED);
		}
	}

	public Match create() {
		destroy();
		return builder.build();
	}

	public static Map<UUID, MatchCreator> getGameCreatorMap() {
		return Collections.unmodifiableMap(GAME_CREATOR_MAP);
	}

	public static MatchCreator getCreator(UUID playerUniqueId) {
		MatchCreator matchCreator = GAME_CREATOR_MAP.get(playerUniqueId);
		if (matchCreator != null) return matchCreator;

		for (MatchCreator creator : GAME_CREATOR_MAP.values()) {
			if (creator.getBuilder().getPlayers().contains(playerUniqueId)) {
				return creator;
			}
		}
		return null;
	}
}
