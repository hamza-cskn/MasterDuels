package mc.obliviate.masterduels.gui.creator;

import mc.obliviate.masterduels.VaultUtil;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.MatchCreator;
import mc.obliviate.masterduels.gui.ConfigurableGui;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.time.Duration;

public class DuelMatchCreatorGUI extends ConfigurableGui {

	private final MatchCreator matchCreator;

	//todo open team manager gui to non-owners
	public DuelMatchCreatorGUI(Player player, MatchCreator matchCreator) {
		super(player, "duel-match-creator-gui");
		this.matchCreator = matchCreator;
	}

	@Override
	public void open() {
		setTitle(MessageUtils.parseColor(MessageUtils.applyPlaceholders(ConfigurationHandler.getMenus().getString(getSectionPath() + ".title"),
				new PlaceholderUtil().add("{mode}", MessageUtils.convertMode(matchCreator.getBuilder().getTeamSize(), matchCreator.getBuilder().getTeamAmount())))));
		setSize(ConfigurationHandler.getMenus().getInt(getSectionPath() + ".size", 5) * 9);
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
			if (matchCreator.getBuilder().getTeamSize() * matchCreator.getBuilder().getTeamAmount() != matchCreator.getBuilder().getPlayers().size()) {
				MessageUtils.sendMessage(player, "game-builder.wrong-player-amount", new PlaceholderUtil().add("{expected}", (matchCreator.getBuilder().getTeamSize() * matchCreator.getBuilder().getTeamAmount()) + "").add("{found}", matchCreator.getBuilder().getPlayers().size() + ""));
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
				.add("{total-players}", matchCreator.getBuilder().getPlayers().size() + ""), e -> {
			new DuelInvitesGUI(player, matchCreator).open();
		});
	}

	private void putKitIcon() {
		/*putIcon("kit", new PlaceholderUtil().add("{kit}", matchCreator.getBuilder().getKit() != null ? matchCreator.getBuilder().getKit().getKitName() : MessageUtils.parseColor(MessageUtils.getMessage("game-creator.none-kit-name"))), e -> {
			new KitSelectionGUI(player, matchCreator.getBuilder(), kit -> {
				open();
			}, MatchCreator.ALLOWED_KITS).open();
		});

		 */
	}

	private void putRoundAmountIcon() {
		putIcon("round-amount", new PlaceholderUtil().add("{round-amount}", matchCreator.getBuilder().getTotalRounds() + ""), e -> {
			if (e.isRightClick()) {
				matchCreator.getBuilder().setTotalRounds(Math.max(matchCreator.getBuilder().getTotalRounds() - 2, MatchCreator.MIN_ROUNDS));
			} else if (e.isLeftClick()) {
				matchCreator.getBuilder().setTotalRounds(Math.min(matchCreator.getBuilder().getTotalRounds() + 2, MatchCreator.MAX_ROUNDS));
			}
			open();
		});
	}

	private void putFinishTimeIcon() {
		putIcon("game-time", new PlaceholderUtil()
				.add("{game-timer}", TimerUtils.formatTimeAsTimer(matchCreator.getBuilder().getDuration().toSeconds()))
				.add("{game-time}", TimerUtils.formatTimeAsTime(matchCreator.getBuilder().getDuration().toSeconds())), e -> {
			if (e.isRightClick()) {
				matchCreator.getBuilder().setDuration(Duration.ofSeconds(Math.max(matchCreator.getBuilder().getDuration().toSeconds() - 30, MatchCreator.MIN_GAME_TIME)));
			} else if (e.isLeftClick()) {
				matchCreator.getBuilder().setDuration(Duration.ofSeconds(Math.min(matchCreator.getBuilder().getDuration().toSeconds() + 30, MatchCreator.MAX_GAME_TIME)));
			}
			open();
		});
	}

	private void putTeamAmountIcon() {
		putIcon("team-amount", new PlaceholderUtil().add("{team-amount}", matchCreator.getBuilder().getTeamAmount() + ""), e -> {
			final int size = matchCreator.getBuilder().getTeamSize();
			if (e.isRightClick()) {
				matchCreator.getBuilder().setTeamsAttributes(size, Math.max(matchCreator.getBuilder().getTeamAmount() - 1, MatchCreator.MAX_TEAM_AMOUNT));
			} else if (e.isLeftClick()) {
				matchCreator.getBuilder().setTeamsAttributes(size, Math.min(matchCreator.getBuilder().getTeamAmount() + 1, MatchCreator.MIN_TEAM_AMOUNT));
			}
			open();
		});
	}

	private void putTeamSizeIcon() {
		putIcon("team-size", new PlaceholderUtil().add("{team-size}", matchCreator.getBuilder().getTeamSize() + ""), e -> {
			final int amount = matchCreator.getBuilder().getTeamAmount();
			if (e.isRightClick()) {
				matchCreator.getBuilder().setTeamsAttributes(Math.max(matchCreator.getBuilder().getTeamSize() - 1, MatchCreator.MIN_TEAM_SIZE), amount);
			} else if (e.isLeftClick()) {
				matchCreator.getBuilder().setTeamsAttributes(Math.min(matchCreator.getBuilder().getTeamSize() + 1, MatchCreator.MAX_TEAM_SIZE), amount);
			}
			open();
		});
	}

	@Override
	public String getSectionPath() {
		return "duel-creator.gui";
	}
}
