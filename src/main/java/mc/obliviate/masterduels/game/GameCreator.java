package mc.obliviate.masterduels.game;

import mc.obliviate.masterduels.MasterDuels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Game Creator classes are player based game builders.
 * they stores and manages invites, owners additional.
 */
public class GameCreator {

	//player uuid, game creator
	private static final Map<UUID, GameCreator> gameCreatorMap = new HashMap<>();
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
		final GameCreator gameCreator = gameCreatorMap.get(ownerPlayer);
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

		gameCreatorMap.put(ownerPlayer, this);
	}

	public static Map<UUID, GameCreator> getGameCreatorMap() {
		return gameCreatorMap;
	}

	public GameBuilder getBuilder() {
		return builder;
	}

	public UUID getOwnerPlayer() {
		return ownerPlayer;
	}

	public void destroy() {
		gameCreatorMap.remove(ownerPlayer);
	}
}
