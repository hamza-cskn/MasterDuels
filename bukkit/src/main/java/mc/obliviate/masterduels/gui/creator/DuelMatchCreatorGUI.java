package mc.obliviate.masterduels.gui.creator;

import com.google.common.base.Preconditions;
import mc.obliviate.masterduels.VaultUtil;
import mc.obliviate.masterduels.data.ConfigurationHandler;
import mc.obliviate.masterduels.game.Match;
import mc.obliviate.masterduels.game.creator.CreatorKitManager;
import mc.obliviate.masterduels.game.creator.MatchCreator;
import mc.obliviate.masterduels.gui.ConfigurableGui;
import mc.obliviate.masterduels.kit.gui.KitSelectionGUI;
import mc.obliviate.masterduels.utils.MessageUtils;
import mc.obliviate.masterduels.utils.placeholder.PlaceholderUtil;
import mc.obliviate.masterduels.utils.timer.TimerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.time.Duration;

public class DuelMatchCreatorGUI extends ConfigurableGui {

	private final MatchCreator matchCreator;
	private final boolean isOwner;

	public DuelMatchCreatorGUI(Player player, MatchCreator matchCreator) {
		super(player, "duel-match-creator-gui");
		this.matchCreator = matchCreator;
		isOwner = player.getUniqueId().equals(matchCreator.getOwnerPlayer());
	}

	@Override
	public void open() {
		setTitle(MessageUtils.parseColor(MessageUtils.applyPlaceholders(ConfigurationHandler.getMenus().getString(getSectionPath() + ".title"),
				new PlaceholderUtil().add("{mode}", MessageUtils.convertMode(matchCreator.getBuilder().getTeamSize(), matchCreator.getBuilder().getTeamAmount())))));
		super.open();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		Preconditions.checkState(matchCreator.getBuilder().getPlayers().size() > 0, "no player found");
		putDysfunctionalIcons(new PlaceholderUtil()
				.add("{mode}", MessageUtils.convertMode(matchCreator.getBuilder().getTeamSize(), matchCreator.getBuilder().getTeamAmount()))
				.add("{invited-players}", matchCreator.getInvites().size() + "")
				.add("{total-players}", matchCreator.getBuilder().getPlayers().size() + "")
				.add("{round-amount}", matchCreator.getBuilder().getTotalRounds() + "")
				.add("{game-timer}", TimerUtils.formatTimeAsTimer(matchCreator.getBuilder().getDuration().toSeconds()))
				.add("{game-time}", TimerUtils.formatTimeAsTime(matchCreator.getBuilder().getDuration().toSeconds()))
				.add("{team-amount}", matchCreator.getBuilder().getTeamAmount() + "")
				.add("{team-size}", matchCreator.getBuilder().getTeamSize() + "")
		);

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
		if (isOwner) {
			putStartGameIcon();
		}
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
		if (matchCreator.getCreatorKitManager().getKitMode().equals(CreatorKitManager.KitMode.VARIOUS)) {
			PlaceholderUtil placeholderUtil = new PlaceholderUtil().add("{your-kit}", matchCreator.getCreatorKitManager().getDefaultKit() == null ? MessageUtils.parseColor(MessageUtils.getMessage("kit.none-kit-name")) : matchCreator.getCreatorKitManager().getDefaultKit().getKitName());
			putIcon("kit-various-mode", placeholderUtil, e -> {
				if (e.isLeftClick()) {
					new KitSelectionGUI(player, matchCreator.getBuilder(), kit -> {
						matchCreator.getCreatorKitManager().setDefaultKit(kit);
						open();
					}, MatchCreator.ALLOWED_KITS).open();
				} else if (e.isRightClick()) {
					matchCreator.getCreatorKitManager().setKitMode(CreatorKitManager.KitMode.MUTUAL);
					open();
				}
			});
		} else {
			PlaceholderUtil placeholderUtil = new PlaceholderUtil().add("{kit}", matchCreator.getCreatorKitManager().getDefaultKit() == null ? MessageUtils.parseColor(MessageUtils.getMessage("kit.none-kit-name")) : matchCreator.getCreatorKitManager().getDefaultKit().getKitName());
			putIcon("kit-mutual-mode", placeholderUtil, e -> {
				if (e.isLeftClick()) {
					new KitSelectionGUI(player, matchCreator.getBuilder(), kit -> {
						matchCreator.getCreatorKitManager().setDefaultKit(kit);
						open();
					}, MatchCreator.ALLOWED_KITS).open();
				} else if (e.isRightClick()) {
					matchCreator.getCreatorKitManager().setKitMode(CreatorKitManager.KitMode.VARIOUS);
					open();
				}
			});
		}
	}

	private void putRoundAmountIcon() {
		putIcon("round-amount", new PlaceholderUtil().add("{round-amount}", matchCreator.getBuilder().getTotalRounds() + ""), e -> {
			if (!isOwner) return;
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
			if (!isOwner) return;
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
			if (!isOwner) return;
			final int size = matchCreator.getBuilder().getTeamSize();
			if (e.isRightClick()) {
				matchCreator.getBuilder().setTeamsAttributes(size, Math.max(matchCreator.getBuilder().getTeamAmount() - 1, MatchCreator.MIN_TEAM_AMOUNT));
			} else if (e.isLeftClick()) {
				matchCreator.getBuilder().setTeamsAttributes(size, Math.min(matchCreator.getBuilder().getTeamAmount() + 1, MatchCreator.MAX_TEAM_AMOUNT));
			}
			open();
		});
	}

	private void putTeamSizeIcon() {
		putIcon("team-size", new PlaceholderUtil().add("{team-size}", matchCreator.getBuilder().getTeamSize() + ""), e -> {
			if (!isOwner) return;
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
