package mc.obliviate.masterduels.game;

import mc.obliviate.masterduels.MasterDuels;
import mc.obliviate.masterduels.api.arena.GameRule;
import mc.obliviate.masterduels.invite.Invite;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Game Creator classes are player based game builders.
 * they stores and manages invites, owners additional.
 */
public class GameCreator {

	//player uuid, game creator
	public static final Map<UUID, GameCreator> GAME_CREATOR_MAP = new HashMap<>();
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
	private final Map<UUID, Invite> invites = new HashMap<>();
	private final UUID ownerPlayer;
	private final GameBuilder builder;
	private final MasterDuels plugin;

	public GameCreator(MasterDuels plugin, UUID ownerPlayer) {
		this.ownerPlayer = ownerPlayer;
		this.builder = new GameBuilder(plugin);
		this.plugin = plugin;

		//check: player uuid is not null
		if (ownerPlayer == null) destroy();

		//check: is game creator already exist
		final GameCreator gameCreator = GAME_CREATOR_MAP.get(ownerPlayer);
		if (gameCreator != null) {
			gameCreator.destroy();
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

	public static Map<UUID, GameCreator> getGameCreatorMap() {
		return GAME_CREATOR_MAP;
	}

	public Map<UUID, Invite> getInvites() {
		return invites;
	}

	public void addPlayer(final Player player) {
		for (final Invite invite : findInvites(player.getUniqueId())) {
			invite.setResult(false);
		}
	}

	public void sendInvite(final Player inviter, final Player invited, final InviteResponse response) {
		if (invited == null) {
			MessageUtils.sendMessage(inviter, "target-is-not-online");
			return;
		}

		//check: is it invite spam
		if (getInvites().containsKey(invited.getUniqueId())) {
			MessageUtils.sendMessage(inviter, "invite.already-invited", new PlaceholderUtil().add("{target}", invited.getName()));
			return;
		}

		final Invite invite = new Invite(plugin, inviter, invited, this);
		invites.put(invited.getUniqueId(), invite);
		invite.onResponse(response);
	}

	public void removeInvite(final UUID uuid) {
		invites.remove(uuid);
	}

	public List<Invite> findInvites(final UUID player) {
		final List<Invite> invites = new ArrayList<>();
		for (final GameCreator creator : GAME_CREATOR_MAP.values()) {
			for (final UUID uuid : creator.getInvites().keySet()) {
				if (uuid.equals(player)) {
					invites.add(creator.getInvites().get(uuid));
				}
			}
		}
		return invites;
	}

	public GameBuilder getBuilder() {
		return builder;
	}

	public UUID getOwnerPlayer() {
		return ownerPlayer;
	}

	public void destroy() {
		GAME_CREATOR_MAP.remove(ownerPlayer);
		for (final Invite invite : invites.values()) {
			invite.onExpire();
		}
	}

	public Game create() {
		destroy();
		return builder.build();
	}
}
