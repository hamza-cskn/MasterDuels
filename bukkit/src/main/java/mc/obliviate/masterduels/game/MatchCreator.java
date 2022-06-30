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
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

/**
 * Game Creator classes are player based game builders.
 * they stores and manages invites, owners additional.
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
		if (ownerPlayer == null) destroy();

		//check: is game creator already exist
		final MatchCreator matchCreator = GAME_CREATOR_MAP.get(ownerPlayer);
		if (matchCreator != null) {
			matchCreator.destroy();
		}

		//check: is player online and is uuid valid.
		final Player player = Bukkit.getPlayer(ownerPlayer);
		if (player == null) {
			destroy();
			return;
		}

		builder.addPlayer(player);

		GAME_CREATOR_MAP.put(ownerPlayer, this);
	}

	public static Map<UUID, MatchCreator> getGameCreatorMap() {
		return GAME_CREATOR_MAP;
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

		final Invite.InviteBuildResult buildResult = Invite.create()
				.setExpireTimeLater(ConfigurationHandler.getConfig().getInt("invite-timeout") * 1000L)
				.setReceiver(target.getUniqueId())
				.setSender(sender.getUniqueId())
				.onResponse(invite -> {
					this.removeInvite(target.getUniqueId());
					response.accept(invite);
				}).build();

		if (buildResult.getInviteBuildState().equals(Invite.InviteBuildState.ERROR_ALREADY_INVITED)) {
			MessageUtils.sendMessage(sender, "invite.already-invited", new PlaceholderUtil().add("{target}", target.getName()));

		} else if (buildResult.getInviteBuildState().equals(Invite.InviteBuildState.SUCCESS)) {
			MessageUtils.sendMessage(sender, "invite.target-has-invited", new PlaceholderUtil().add("{target}", target.getName()).add("{expire-time}", TimerUtils.formatTimeUntilThenAsTimer(buildResult.getInvite().getExpireOutTime()) + ""));
			InviteUtils.sendInviteMessage(buildResult.getInvite(), MessageUtils.getMessageConfig().getStringList("invite.game-creator-invite-text"));
			invites.put(target.getUniqueId(), buildResult.getInvite());
		}
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
		builder.destroy();
	}

	public Match create() {
		destroy();
		return builder.build();
	}
}
