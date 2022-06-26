package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.masterduels.VaultUtil;
import mc.obliviate.masterduels.data.YamlStorageHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchBuilder;
import mc.obliviate.masterduels.game.MatchCreator;
import mc.obliviate.masterduels.gui.ConfigurableGui;
import mc.obliviate.masterduels.kit.gui.KitSelectionGUI;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.time.Duration;

public class DuelMatchCreatorGUI extends ConfigurableGui {

	private final MatchBuilder matchBuilder;
	private final MatchCreator matchCreator;

	//todo open team manager gui to non-owners
	public DuelMatchCreatorGUI(Player player, MatchCreator matchCreator) {
		super(player, "duel-match-creator-gui");
		this.matchBuilder = matchCreator.getBuilder();
		this.matchCreator = matchCreator;
	}

	@Override
	public void open() {
		setTitle(MessageUtils.parseColor(MessageUtils.applyPlaceholders(YamlStorageHandler.getConfig().getString(getSectionPath() + ".title"),
				new PlaceholderUtil().add("{mode}", MessageUtils.convertMode(matchBuilder.getTeamSize(), matchBuilder.getTeamAmount())))));
		setSize(YamlStorageHandler.getConfig().getInt(getSectionPath() + ".size", 5) * 9);
		super.open();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		putDysfunctionalIcons();

		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.teamsize"))
			putTeamSizeIcon();
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.rules"))
			putRulesIcon();
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.teamamount"))
			putTeamAmountIcon();
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.teammanager"))
			putTeamManagerIcon();
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.roundamount"))
			putRoundAmountIcon();
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.kit"))
			putKitIcon();
		if (VaultUtil.checkPermission(player, "masterduels.duelcreator.set.finishtime"))
			putFinishTimeIcon();

		putInvitesIcon();
		putStartGameIcon();
	}

	private void putStartGameIcon() {
		putIcon("start-game", e -> {
			if (matchBuilder.getTeamSize() * matchBuilder.getTeamAmount() != matchBuilder.getPlayers().size()) {
				MessageUtils.sendMessage(player, "game-builder.wrong-player-amount", new PlaceholderUtil().add("{expected}", (matchBuilder.getTeamSize() * matchBuilder.getTeamAmount()) + "").add("{found}", matchBuilder.getPlayers().size() + ""));
				return;
			}

			final Match game = matchCreator.create();
			if (game == null) {
				MessageUtils.sendMessage(player, "no-arena-found");
				return;
			}
			game.start();
		});
	}

	public void putTeamManagerIcon() {
		putIcon("manage-teams", e -> {
			new DuelTeamManagerGUI(player, matchCreator).open();
		});
	}

	private void putRulesIcon() {
		putIcon("rules", e -> {
			new DuelSettingsGUI(player, matchCreator).open();
		});
	}

	private void putInvitesIcon() {
		putIcon("invites", new PlaceholderUtil()
				.add("{invited-players}", matchCreator.getInvites().size() + "")
				.add("{total-players}", matchBuilder.getPlayers().size() + ""), e -> {
			new DuelInvitesGUI(player, matchCreator).open();
		});
	}

	private void putKitIcon() {
		putIcon("kit", new PlaceholderUtil().add("{kit}", matchBuilder.getKit() != null ? matchBuilder.getKit().getKitName() : MessageUtils.parseColor(MessageUtils.getMessage("game-creator.none-kit-name"))), e -> {
			new KitSelectionGUI(player, matchBuilder, kit -> {
				open();
			}, MatchCreator.ALLOWED_KITS).open();
		});
	}

	private void putRoundAmountIcon() {
		putIcon("round-amount", new PlaceholderUtil().add("{round-amount}", matchBuilder.getTotalRounds() + ""), e -> {
			if (e.isRightClick()) {
				matchBuilder.setTotalRounds(Math.max(matchBuilder.getTotalRounds() - 2, MatchCreator.MIN_ROUNDS));
			} else if (e.isLeftClick()) {
				matchBuilder.setTotalRounds(Math.min(matchBuilder.getTotalRounds() + 2, MatchCreator.MAX_ROUNDS));
			}
			open();
		});
	}

	private void putFinishTimeIcon() {
		putIcon("game-time", new PlaceholderUtil()
				.add("{game-timer}", TimerUtils.formatTimeAsTimer(matchBuilder.getMatchDuration().toSeconds()))
				.add("{game-time}", TimerUtils.formatTimeAsTime(matchBuilder.getMatchDuration().toSeconds())), e -> {
			if (e.isRightClick()) {
				matchBuilder.setMatchDuration(Duration.ofSeconds(Math.max(matchBuilder.getMatchDuration().toSeconds() - 30, MatchCreator.MIN_GAME_TIME)));
			} else if (e.isLeftClick()) {
				matchBuilder.setMatchDuration(Duration.ofSeconds(Math.min(matchBuilder.getMatchDuration().toSeconds() + 30, MatchCreator.MAX_GAME_TIME)));
			}
			open();
		});
	}

	private void putTeamAmountIcon() {
		putIcon("team-amount", new PlaceholderUtil().add("{team-amount}", matchBuilder.getTeamAmount() + ""), e -> {
			if (e.isRightClick()) {
				matchBuilder.setTeamAmount(Math.max(matchBuilder.getTeamAmount() - 1, MatchCreator.MAX_TEAM_AMOUNT));
			} else if (e.isLeftClick()) {
				matchBuilder.setTeamAmount(Math.min(matchBuilder.getTeamAmount() + 1, MatchCreator.MIN_TEAM_AMOUNT));
			}
			open();
		});
	}

	private void putTeamSizeIcon() {
		putIcon("team-size", new PlaceholderUtil().add("{team-size}", matchBuilder.getTeamSize() + ""), e -> {
			if (e.isRightClick()) {
				matchBuilder.setTeamSize(Math.max(matchBuilder.getTeamSize() - 1, MatchCreator.MIN_TEAM_SIZE));
			} else if (e.isLeftClick()) {
				matchBuilder.setTeamSize(Math.min(matchBuilder.getTeamSize() + 1, MatchCreator.MAX_TEAM_SIZE));
			}
			open();
		});
	}

	@Override
	public String getSectionPath() {
		return "duel-creator.gui";
	}
}
