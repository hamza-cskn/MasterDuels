package mc.obliviate.masterduels.game;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.arena.Arena;
import mc.obliviate.masterduels.game.creator.MatchCreator;
import mc.obliviate.masterduels.game.gamerule.GameRule;
import mc.obliviate.masterduels.kit.Kit;
import mc.obliviate.masterduels.user.IUser;
import mc.obliviate.masterduels.user.Member;
import mc.obliviate.masterduels.user.UserHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * this object uses match field as lock
 * when match field is not null, match builder is locked.
 **/
public class MatchBuilder {

	private Match match = null;
	private final List<UUID> players = new ArrayList<>();
	private final MatchDataStorage matchDataStorage;

	protected MatchBuilder() {
		this.matchDataStorage = new MatchDataStorage();
	}

	protected MatchBuilder(MatchDataStorage matchDataStorage) {
		this.matchDataStorage = matchDataStorage;
	}

	public Match build() {
		return build(new ArrayList<>());
	}

	public Match build(List<String> allowedMaps) {
		final Arena arena = Arena.findArena(matchDataStorage.getGameTeamManager().getTeamSize(), matchDataStorage.getGameTeamManager().getTeamAmount(), allowedMaps);

		if (arena == null) {
			//arena could not found
			return null;
		}

		if (match != null) {
			throw new IllegalStateException("Match Builder already built before.");
		}

		final Match match = new Match(arena, matchDataStorage);
		matchDataStorage.lock(match);
		Preconditions.checkState(matchDataStorage.getGameTeamManager().getAllMembers().size() > 0, "there is no any player");

		this.match = match;
		return match;
	}

	public void addPlayer(final Player player) {
		addPlayer(player, null, -1);
	}

	public void addPlayer(final Player player, Kit kit) {
		addPlayer(player, kit, -1);
	}

	public void addPlayer(final Player player, Kit kit, int teamNo) {
		Preconditions.checkState(!isLocked(), "this object is locked");
		Preconditions.checkArgument(player.isOnline(), "offline player couldn't add");

		final IUser user = UserHandler.getUser(player.getUniqueId());
		Preconditions.checkState(!(user instanceof Member), "this player already is in a duel game");

		final int availableTeamNo = teamNo > 0 ? teamNo : getAvailableTeamNo();
		Preconditions.checkState(availableTeamNo > 0, "any available team not found.");

		if (user.isInMatchBuilder()) {
			user.getMatchBuilder().removePlayer(user);
			MatchCreator.cleanKillCreator(player.getUniqueId());
		}

		user.setMatchBuilder(this);

		players.add(player.getUniqueId());
		matchDataStorage.getGameTeamManager().registerPlayer(player, kit, availableTeamNo);
	}

	public void removePlayer(IUser user) {
		players.remove(user.getPlayer().getUniqueId());
		matchDataStorage.getGameTeamManager().unregisterPlayer(user);
		user.exitMatchBuilder();
	}

	public void removePlayer(Player player) {
		final IUser user = UserHandler.getUser(player.getUniqueId());
		removePlayer(user);
	}

	public List<UUID> getPlayers() {
		return Collections.unmodifiableList(players);
	}

	private void randomizeTeams() {
		if (isLocked()) throw new IllegalStateException("this object is locked");

		final int size = matchDataStorage.getGameTeamManager().getTeamSize();
		final int amount = matchDataStorage.getGameTeamManager().getTeamAmount();

		for (int teamNo = 1; teamNo <= amount; teamNo++) {
			final List<UUID> playerList = players.subList(Math.min(players.size(), (teamNo - 1) * size), Math.min(players.size(), teamNo * size));
			for (UUID uuid : playerList) {
				final Player player = Bukkit.getPlayer(uuid);
				Preconditions.checkNotNull(player, "player cannot be offline");
				Member.Builder memberBuilder = matchDataStorage.getGameTeamManager().getMemberBuilder(player.getUniqueId());
				matchDataStorage.getGameTeamManager().registerPlayer(player, memberBuilder.getKit(), teamNo);
			}
		}
	}

	public int getAvailableTeamNo() {
		for (Team.Builder teamBuilder : matchDataStorage.getGameTeamManager().getTeamBuilders()) {
			if (teamBuilder.getMemberBuilders().size() < matchDataStorage.getGameTeamManager().getTeamSize())
				return teamBuilder.getTeamId();
		}
		return -1;
	}

	public Team getTeam(Player player) {
		return matchDataStorage.getGameTeamManager().getTeam(player);
	}

	public MatchBuilder setTeamsAttributes(int size, int amount) {
		matchDataStorage.getGameTeamManager().setTeamsAttributes(size, amount);
		//randomizeTeams();
		return this;
	}

	public int getTeamSize() {
		return matchDataStorage.getGameTeamManager().getTeamSize();
	}

	public int getTeamAmount() {
		return matchDataStorage.getGameTeamManager().getTeamAmount();
	}

	public MatchBuilder setDuration(Duration duration) {
		matchDataStorage.setMatchDuration(duration);
		return this;
	}

	public Duration getDuration() {
		return matchDataStorage.getMatchDuration();
	}

	public int getTotalRounds() {
		return matchDataStorage.getRoundData().getTotalRounds();
	}

	public MatchBuilder setTotalRounds(int totalRounds) {
		matchDataStorage.getGameRoundData().setTotalRounds(totalRounds);
		return this;
	}

	public MatchBuilder addRule(GameRule rule) {
		matchDataStorage.addRule(rule);
		return this;
	}

	public List<GameRule> getRules() {
		return matchDataStorage.getGameRules();
	}

	public MatchBuilder removeRule(GameRule rule) {
		matchDataStorage.removeRule(rule);
		return this;
	}

	public MatchBuilder clearRules() {
		matchDataStorage.clearRules();
		return this;
	}

	public MatchDataStorage getData() {
		return matchDataStorage;
	}

	public boolean isLocked() {
		return match != null;
	}

	public void destroy() {
		//unregister game builder
		for (UUID uuid : new ArrayList<>(players)) {
			UserHandler.getUser(uuid).exitMatchBuilder();

		}
	}

}
